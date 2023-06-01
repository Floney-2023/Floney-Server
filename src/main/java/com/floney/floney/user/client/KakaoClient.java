package com.floney.floney.user.client;

import com.floney.floney.common.exception.OAuthResponseException;
import com.floney.floney.common.exception.OAuthTokenNotValidException;
import com.floney.floney.user.dto.response.KakaoUserResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Component
public class KakaoClient implements ClientProxy {

    private String id;

    @Override
    public void init(String authToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode(StandardCharsets.UTF_8)
                .build().toUri();

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer ".concat(authToken));
        HttpEntity<String> request = new HttpEntity<>(header);

        try {
            ResponseEntity<KakaoUserResponse> result = createRequest()
                    .exchange(uri, HttpMethod.GET, request, KakaoUserResponse.class);
            this.id = result.getBody().getId().toString();
        } catch (HttpClientErrorException.Unauthorized exception) {
            throw new OAuthTokenNotValidException();
        } catch (NullPointerException exception) {
            throw new OAuthResponseException();
        }

    }

}
