package com.floney.floney.user.client;

import com.floney.floney.common.exception.OAuthResponseException;
import com.floney.floney.user.dto.response.GoogleUserResponse;
import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleClient implements ClientProxy {

    @Override
    public String getEmail(String authToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com")
                .path("/tokeninfo")
                .queryParam("id_token", authToken)
                .build().toUri();

        RestTemplate restTemplate = new RestTemplate();
        GoogleUserResponse result = restTemplate.getForObject(uri, GoogleUserResponse.class);

        if(result == null) {
            throw new OAuthResponseException();
        }
        result.validate();

        return result.getEmail();
    }

}
