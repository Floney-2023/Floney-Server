package com.floney.floney.user.client;

import com.floney.floney.common.exception.OAuthResponseException;
import com.floney.floney.common.exception.OAuthTokenNotValidException;
import com.floney.floney.user.dto.response.GoogleUserResponse;
import java.net.URI;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Component
public class GoogleClient implements ClientProxy {

    private Long id;

    @Override
    public void init(String authToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com")
                .path("/tokeninfo")
                .queryParam("id_token", authToken)
                .build().toUri();

        RestTemplate restTemplate = new RestTemplate();
        GoogleUserResponse result;
        try {
            result = restTemplate.getForObject(uri, GoogleUserResponse.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new OAuthTokenNotValidException();
        }

        if(result == null) {
            throw new OAuthResponseException();
        }

        this.id = Long.valueOf(result.getSub());

    }

}
