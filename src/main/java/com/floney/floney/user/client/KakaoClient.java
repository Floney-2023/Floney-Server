package com.floney.floney.user.client;

import com.floney.floney.common.exception.OAuthResponseException;
import com.floney.floney.user.dto.response.KakaoUserResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Component
public class KakaoClient implements ClientProxy {

    private Long id;
    private String email;
    private String nickname;

    @Override
    public void init(String authToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode(StandardCharsets.UTF_8)
                .build().toUri();

        RestTemplate restTemplate = new RestTemplate();
        KakaoUserResponse result = restTemplate.getForObject(uri, KakaoUserResponse.class);

        if(result == null) {
            throw new OAuthResponseException();
        }

        this.id = result.getId();
        this.email = result.getKakaoAccount().getEmail();
        this.nickname = result.getKakaoAccount().getProfile().getNickname();

    }

}
