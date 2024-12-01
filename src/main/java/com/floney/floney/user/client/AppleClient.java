package com.floney.floney.user.client;

import com.apple.itunes.storekit.model.HistoryResponse;
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.apple.itunes.storekit.model.ResponseBodyV2;
import com.apple.itunes.storekit.model.ResponseBodyV2DecodedPayload;
import com.apple.itunes.storekit.verification.VerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floney.floney.common.exception.user.OAuthResponseException;
import com.floney.floney.common.exception.user.OAuthTokenNotValidException;
import com.floney.floney.common.util.AppleJwtProvider;
import com.floney.floney.subscribe.entity.AppleSubscribe;
import com.floney.floney.subscribe.repository.AppleSubscribeRepository;
import com.floney.floney.subscribe.dto.GetTransactionResponse;
import com.floney.floney.user.client.dto.ApplePublicKeys;
import com.floney.floney.user.client.dto.AppleTokenHeader;
import com.floney.floney.user.client.util.AppleOAuthPublicKeyGenerator;
import com.floney.floney.user.entity.User;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleClient implements ClientProxy {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final AppleOAuthPublicKeyGenerator publicKeyGenerator;
    private final AppleJwtProvider appleJwtProvider;
    private final AppleSubscribeRepository subscribeRepository;

    @Value("${apple.transaction-url}")
    private String transactionUrl;

    public GetTransactionResponse getTransaction(String transactionId, User user) throws IOException {
        String token = appleJwtProvider.getAppleJwt();
        System.out.println(token);
        Map<String, String> params = new HashMap<>();
        params.put("transactionId", transactionId);
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + token);
        header.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(header);

        try {
            HistoryResponse response = restTemplate.exchange(this.transactionUrl, HttpMethod.GET, entity, HistoryResponse.class, params).getBody();
            JWSTransactionDecodedPayload payload = this.appleJwtProvider.parseTransaction(response.getSignedTransactions().get(0));
            AppleSubscribe subscribe = new AppleSubscribe(payload, user);
            this.subscribeRepository.save(subscribe);
            return new GetTransactionResponse(true);
        } catch (Exception exception) {
            logger.error("apple get transaction error transaction id = {} email = {},{}", transactionId, user.getEmail(), exception.getMessage());
            return new GetTransactionResponse(false);
        }
    }

    @Override
    public String getAuthId(final String authToken) {
        try {
            final AppleTokenHeader authTokenHeader = objectMapper.readValue(
                extractHeaderFrom(authToken), AppleTokenHeader.class
            );

            final URI uri = UriComponentsBuilder
                .fromUriString("https://appleid.apple.com")
                .path("/auth/keys")
                .build().toUri();

            logger.info("[{}]로 통신 시작", uri);
            final ApplePublicKeys applePublicKeys = restTemplate.getForObject(uri, ApplePublicKeys.class);

            if (applePublicKeys == null) {
                throw new OAuthResponseException();
            }

            final PublicKey publicKey = publicKeyGenerator.generate(authTokenHeader, applePublicKeys.getKeys());
            return parseAuthId(authToken, publicKey);
        } catch (final JsonProcessingException exception) {
            throw new OAuthTokenNotValidException();

        }
    }

    private String parseAuthId(final String authToken, final PublicKey publicKey) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build()
            .parseClaimsJws(authToken)
            .getBody()
            .getSubject();
    }

    private String extractHeaderFrom(final String token) {
        final String header = token.split("\\.")[0];
        return new String(Base64Utils.decodeFromString(header));
    }

    public void callback(ResponseBodyV2 responseBodyV2) throws VerificationException, IOException {
        String payload = responseBodyV2.getSignedPayload();
        ResponseBodyV2DecodedPayload decodedPayload= this.appleJwtProvider.parseNotification(payload);

        //타입별로
       logger.info("result callback"+decodedPayload);
        String transaction = decodedPayload.getData().getSignedTransactionInfo();
        if(transaction != null) {
            JWSTransactionDecodedPayload result = this.appleJwtProvider.parseTransaction(transaction);
            logger.info("result callback 2 = " + result);
        }
    }
}
