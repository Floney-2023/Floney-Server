package com.floney.floney.user.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floney.floney.common.exception.book.NotFoundAlarmException;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.subscribe.dto.GoogleCallbackDto;
import com.floney.floney.subscribe.dto.GoogleRtndDto;
import com.floney.floney.subscribe.entity.AndroidSubscribe;
import com.floney.floney.subscribe.entity.AppleSubscribe;
import com.floney.floney.subscribe.repository.AndroidSubscribeRepository;
import com.floney.floney.subscribe.dto.GetTransactionResponse;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import com.google.api.gax.rpc.NotFoundException;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.jsonwebtoken.io.IOException;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.persistence.NoResultException;
import java.nio.charset.StandardCharsets;


@Component
@RequiredArgsConstructor
public class AndroidClient {

    private final RestTemplate restTemplate;
    private final AndroidSubscribeRepository androidSubscribeRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());


    public ResponseEntity<Map> getSubscriptionsFromAndroid(String tokenId) throws java.io.IOException {
        String url = "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/subscriptions/{subscriptionId}/tokens/{token}";
        String authToken = this.getToken();
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + authToken);
        header.set("Content-Type", "application/json");

        Map<String, String> params = new HashMap<>();
        params.put("packageName", "com.aos.floney");
        params.put("token", tokenId);
        params.put("subscriptionId", "floney_plus");

        HttpEntity<String> entity = new HttpEntity<>(header);

        return restTemplate.exchange(url, HttpMethod.GET, entity, Map.class, params);
    }

    public GetTransactionResponse getTransaction(User user, String tokenId) throws java.io.IOException {
        ResponseEntity<Map> androidSubscriptionPurchase = getSubscriptionsFromAndroid(tokenId);
        logger.info("callback {} ", androidSubscriptionPurchase);

        Map<String, Object> body = androidSubscriptionPurchase.getBody();
        Object cancelReason = body != null ? body.get("cancelReason") : null;
        Object paymentState = body != null ? body.get("paymentState") : null;
        Object obfuscatedExternalAccountId = body != null ? body.get("obfuscatedExternalAccountId") : null;

        // obfuscatedExternalAccountId로 유저 찾기 (이메일을 뒤집은 값)
        if (obfuscatedExternalAccountId != null && user == null) {
            String reversedEmail = new StringBuilder(obfuscatedExternalAccountId.toString()).reverse().toString();
            user = userRepository.findByEmail(reversedEmail).orElse(null);
        }

        if ((paymentState != null && paymentState.equals(1)) || cancelReason != null) {
            Object orderId = androidSubscriptionPurchase.getBody().get("orderId");
            String orderIdWithoutIndex = orderId.toString().replaceAll("\\.+[0-9]+$", "");
            logger.info("orderIdWithoutIndex {} ",orderIdWithoutIndex);

            Optional<AndroidSubscribe> androidSubscribe = user != null ? 
                this.androidSubscribeRepository.findFirstAndroidSubscribeByUserOrderByUpdatedAtDesc(user) :
                this.androidSubscribeRepository.findAndroidSubscribeByOrderId(orderIdWithoutIndex);

            AndroidSubscribe savedSubscribe;
            if (androidSubscribe.isEmpty()) {
                AndroidSubscribe subscribe = new AndroidSubscribe(androidSubscriptionPurchase.getBody(), user);
                androidSubscribeRepository.save(subscribe);
                logger.info("create success in get tx");
            } else {
                savedSubscribe = androidSubscribe.get();
                savedSubscribe.update(androidSubscriptionPurchase.getBody(),user);
                this.androidSubscribeRepository.save(savedSubscribe);
                logger.info("update success in get tx");
            }
            return new GetTransactionResponse(true);
        } else {
            return new GetTransactionResponse(false);
        }
    }

    private String getToken() throws IOException, java.io.IOException {
        ClassPathResource resource = new ClassPathResource("/secrets/floney-android.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream())
            .createScoped(Collections.singleton("https://www.googleapis.com/auth/androidpublisher"));

        AccessToken accessToken = credentials.refreshAccessToken();
        return accessToken.getTokenValue();
    }

    public void callback(GoogleCallbackDto payload) throws java.io.IOException {
        byte[] dataBytes = payload.getMessage().getData().getBytes(StandardCharsets.UTF_8);
        String encodedData = new String(dataBytes, StandardCharsets.UTF_8);
        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);

        // 디코딩된 바이트 배열을 문자열로 변환
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        // JSON 문자열을 DTO 클래스에 매핑
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleRtndDto dto = objectMapper.readValue(decodedString, GoogleRtndDto.class);

        String purchaseToken = "";
        if (dto.getSubscriptionNotification() != null) {
            purchaseToken = dto.getSubscriptionNotification().getPurchaseToken();
        } else if (dto.getVoidedPurchaseNotification() != null) {
            purchaseToken = dto.getVoidedPurchaseNotification().getPurchaseToken();
        } else {
            return;
        }

        // 새 토큰으로 Google Play API 호출하여 linkedPurchaseToken 확인
        ResponseEntity<Map> subscriptionInfo = getSubscriptionsFromAndroid(purchaseToken);
        Map<String, Object> body = subscriptionInfo.getBody();
        
        if (body != null && body.containsKey("linkedPurchaseToken")) {
            String linkedPurchaseToken = (String) body.get("linkedPurchaseToken");
            
            // linkedPurchaseToken으로 기존 구독 찾기
            Optional<AndroidSubscribe> existingSubscription = findSubscriptionByPurchaseToken(linkedPurchaseToken);
            
            if (existingSubscription.isPresent()) {
                // 기존 구독을 새 토큰으로 업데이트
                AndroidSubscribe subscription = existingSubscription.get();
                subscription.update(body, subscription.getUser());
                androidSubscribeRepository.save(subscription);
                logger.info("Subscription renewed with new token: {} -> {}", linkedPurchaseToken, purchaseToken);
                return;
            }
        }

        // linkedPurchaseToken이 없거나 기존 구독을 찾지 못한 경우 기존 로직 수행
        this.getTransaction(null, purchaseToken);
        logger.info("Decoded DTO 결과: " + decodedString);
    }
    
    private Optional<AndroidSubscribe> findSubscriptionByPurchaseToken(String purchaseToken) {
        return androidSubscribeRepository.findAll().stream()
            .filter(subscription -> purchaseToken.equals(subscription.getOrderId()))
            .findFirst();
    }

    public GetTransactionResponse isSubscribe(User user) {
        Optional<AndroidSubscribe> subscribe = this.androidSubscribeRepository.findFirstAndroidSubscribeByUserOrderByUpdatedAtDesc(user);
        long currentTimeMillis = new Date().getTime();

        if (subscribe.isPresent()) {
            if (subscribe.get().getExpiryTimeMillis() != null
                && Long.parseLong(subscribe.get().getExpiryTimeMillis()) >= currentTimeMillis) {
                return new GetTransactionResponse(true);
            }
        }

        return new GetTransactionResponse(false);
    }

    public AndroidSubscribe getAndroidSubscribe(User user){
        AndroidSubscribe and =  this.androidSubscribeRepository.findFirstAndroidSubscribeByUserOrderByUpdatedAtDesc(user)
            .orElseThrow(NotFoundBookLineException::new);
        return and;
    }

}