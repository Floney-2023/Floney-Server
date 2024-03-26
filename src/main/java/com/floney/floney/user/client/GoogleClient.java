package com.floney.floney.user.client;

import com.floney.floney.common.exception.user.EmptyOAuthResponseException;
import com.floney.floney.common.exception.user.OAuthTokenNotValidException;
import com.floney.floney.user.dto.response.GoogleUserResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class GoogleClient implements ClientProxy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final RestTemplate restTemplate;

    @Override
    public String getAuthId(final String authToken) {
        final URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com")
                .path("/tokeninfo")
                .queryParam("id_token", authToken)
                .build().toUri();

        try {
            logger.info("[{}]로 통신 시작", uri);
            final GoogleUserResponse result = restTemplate.getForObject(uri, GoogleUserResponse.class);
            return result.getSub();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new OAuthTokenNotValidException();
        } catch (NullPointerException exception) {
            throw new EmptyOAuthResponseException();
        }
    }
}
