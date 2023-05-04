package com.floney.floney.common;

import com.floney.floney.common.token.JwtTokenProvider;
import com.floney.floney.common.token.dto.TokenDto;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${client.url}")
    private String baseUri;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        String uri = UriComponentsBuilder.fromUriString(baseUri)
                .queryParam("token", tokenDto.getAccessToken())
                .build().toUriString();

        if (!response.isCommitted()) {
            getRedirectStrategy().sendRedirect(request, response, uri);
        }
    }

}
