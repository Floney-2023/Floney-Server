package com.floney.floney.user.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floney.floney.subscribe.dto.GoogleCallbackDto;
import com.floney.floney.subscribe.dto.GoogleRtndDto;
import com.floney.floney.subscribe.entity.AndroidSubscribe;
import com.floney.floney.subscribe.repository.AndroidSubscribeRepository;
import com.floney.floney.subscribe.dto.GetTransactionResponse;
import com.floney.floney.user.entity.User;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.jsonwebtoken.io.IOException;
import java.util.Base64;

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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class AndroidClient {

    private final RestTemplate restTemplate;
    private final AndroidSubscribeRepository androidSubscribeRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public GetTransactionResponse getTransaction(User user, String tokenId) throws java.io.IOException {
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

        ResponseEntity<Map> androidSubscriptionPurchase = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class, params);

        if (androidSubscriptionPurchase.getBody().get("paymentState") == "1") {
            AndroidSubscribe subscribe = new AndroidSubscribe(androidSubscriptionPurchase.getBody(), user);
            androidSubscribeRepository.save(subscribe);
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

    public void callback(GoogleCallbackDto payload) throws JsonProcessingException {
        byte[] dataBytes = payload.getMessage().getData().getBytes(StandardCharsets.UTF_8);
        String encodedData = new String(dataBytes, StandardCharsets.UTF_8);
        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);

        // 디코딩된 바이트 배열을 문자열로 변환
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        // JSON 문자열을 DTO 클래스에 매핑
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleRtndDto dto = objectMapper.readValue(decodedString, GoogleRtndDto.class);

        // 결과 로그 출력
        logger.info("Decoded DTO 결과: " + dto);
        logger.info("subscription" + dto.getSubscriptionNotification());
        // logger.info(dto.getSubscriptionNotification().getPurchaseToken());
    }

}