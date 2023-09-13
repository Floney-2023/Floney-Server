package com.floney.floney.user.client;

import com.floney.floney.common.exception.user.OAuthResponseException;
import com.floney.floney.common.exception.user.OAuthTokenNotValidException;
import com.floney.floney.user.dto.response.KakaoUserResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoClient implements ClientProxy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final RestTemplate restTemplate;

    @Override
    public String getAuthId(final String authToken) {
        final URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode(StandardCharsets.UTF_8)
                .build().toUri();

        final HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer ".concat(authToken));
        final HttpEntity<String> request = new HttpEntity<>(header);

        try {
            logger.info("[{}]로 통신 시작", uri);
            final ResponseEntity<KakaoUserResponse> result = restTemplate
                    .exchange(uri, HttpMethod.GET, request, KakaoUserResponse.class);
            return Objects.requireNonNull(result.getBody()).getId().toString();
        } catch (HttpClientErrorException.Unauthorized exception) {
            throw new OAuthTokenNotValidException();
        } catch (NullPointerException exception) {
            throw new OAuthResponseException();
        }
    }
}
