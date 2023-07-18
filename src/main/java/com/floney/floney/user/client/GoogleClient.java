package com.floney.floney.user.client;

import com.floney.floney.common.exception.user.OAuthResponseException;
import com.floney.floney.common.exception.user.OAuthTokenNotValidException;
import com.floney.floney.user.dto.response.GoogleUserResponse;
import java.net.URI;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Component
public class GoogleClient implements ClientProxy {

    private String id;

    @Override
    public void init(String authToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com")
                .path("/tokeninfo")
                .queryParam("id_token", authToken)
                .build().toUri();

        try {
            GoogleUserResponse result = createRequest().getForObject(uri, GoogleUserResponse.class);
            this.id = result.getSub();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new OAuthTokenNotValidException();
        } catch (NullPointerException exception) {
            throw new OAuthResponseException();
        }

    }

}
