package com.floney.floney.user.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floney.floney.common.exception.user.EmptyOAuthResponseException;
import com.floney.floney.common.exception.user.OAuthTokenNotValidException;
import com.floney.floney.user.client.dto.ApplePublicKeys;
import com.floney.floney.user.client.dto.AppleTokenHeader;
import com.floney.floney.user.client.util.AppleOAuthPublicKeyGenerator;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.PublicKey;

@Component
@RequiredArgsConstructor
public class AppleClient implements ClientProxy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final AppleOAuthPublicKeyGenerator publicKeyGenerator;

    @Override
    public String getAuthId(final String authToken) {
        try {
            final AppleTokenHeader authTokenHeader = objectMapper.readValue(
                    extractHeaderFrom(authToken), AppleTokenHeader.class
            );

            final URI uri = UriComponentsBuilder
                    .fromUriString("https://appleid.apple.com")
                    .path("/auth/keys")
                    .build().toUri();

            logger.info("[{}]로 통신 시작", uri);
            final ApplePublicKeys applePublicKeys = restTemplate.getForObject(uri, ApplePublicKeys.class);

            if (applePublicKeys == null) {
                throw new EmptyOAuthResponseException();
            }

            final PublicKey publicKey = publicKeyGenerator.generate(authTokenHeader, applePublicKeys.getKeys());
            return parseAuthId(authToken, publicKey);
        } catch (final JsonProcessingException exception) {
            throw new OAuthTokenNotValidException();
        }
    }

    private String parseAuthId(final String authToken, final PublicKey publicKey) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build()
                .parseClaimsJws(authToken)
                .getBody()
                .getSubject();
    }

    private String extractHeaderFrom(final String token) {
        final String header = token.split("\\.")[0];
        return new String(Base64Utils.decodeFromString(header));
    }
}
