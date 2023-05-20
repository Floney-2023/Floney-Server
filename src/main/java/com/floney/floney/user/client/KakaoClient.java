package com.floney.floney.user.client;

import com.floney.floney.user.dto.response.KakaoUserResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class KakaoClient implements ClientProxy {

    @Override
    public String getEmail(String authToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode(StandardCharsets.UTF_8)
                .build().toUri();

        RestTemplate restTemplate = new RestTemplate();
        KakaoUserResponse result = restTemplate.getForObject(uri, KakaoUserResponse.class);
        result.validate();

        return result.getKakaoAccount().getEmail();
    }

}
