package com.floney.floney.user.client;

import com.floney.floney.subscribe.entity.AndroidSubscribe;
import com.floney.floney.subscribe.repository.AndroidSubscribeRepository;
import com.floney.floney.subscribe.dto.GetTransactionResponse;
import com.floney.floney.subscribe.dto.AndroidSubscriptionPurchase;
import com.floney.floney.user.entity.User;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class AndroidClient {

    private final RestTemplate restTemplate;
    private final AndroidSubscribeRepository androidSubscribeRepository;

    public GetTransactionResponse getTransaction(User user,String tokenId) throws java.io.IOException {
        String url = "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/subscriptions/{subscriptionId}/tokens/{token}";
        String authToken = this.getToken();
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + authToken);
        header.set("Content-Type", "application/json");

        Map<String, String> params = new HashMap<>();
        params.put("packageName", "com.aos.floney");
        params.put("token",tokenId);
        params.put("subscriptionId","floney_plus");

        HttpEntity<String> entity = new HttpEntity<>(header);

        AndroidSubscriptionPurchase androidSubscriptionPurchase = restTemplate.exchange(url, HttpMethod.GET, entity, AndroidSubscriptionPurchase.class,params).getBody();

        if (androidSubscriptionPurchase.getPaymentState() == 1){
            AndroidSubscribe subscribe = new AndroidSubscribe(androidSubscriptionPurchase, user);
            androidSubscribeRepository.save(subscribe);
            return new GetTransactionResponse(true);
        }
        return new GetTransactionResponse(false);
    }

    private String getToken() throws IOException, java.io.IOException {
        ClassPathResource resource = new ClassPathResource("/secrets/floney-android.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream())
            .createScoped(Collections.singleton("https://www.googleapis.com/auth/androidpublisher"));

        AccessToken accessToken =  credentials.refreshAccessToken();
        return accessToken.getTokenValue();
    }
}