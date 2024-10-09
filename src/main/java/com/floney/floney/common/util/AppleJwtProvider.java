package com.floney.floney.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppleJwtProvider {
    private final String appleKid;
    private final String appleTokenFileName;
    private String issuer;
    private String claim;

    public AppleJwtProvider(
        @Value("${apple.kid}") String appleKid,
        @Value("${apple.tokenFileName}") String tokenFileName,
        @Value("${apple.issuer}") String issuer,
        @Value("${apple.bundle-id}") String claim
    ) {
        this.appleKid = appleKid;
        this.appleTokenFileName = "secrets/" + tokenFileName;
        this.issuer = issuer;
        this.claim = claim;
    }

    public String getAppleJwt() throws IOException {
        Map<String, Object> header = this.getHeader();
        Instant now = Instant.now();
        PrivateKey privateKey = this.getPrivateKey();

        return Jwts.builder()
            .setHeader(header)
            .setIssuer(this.issuer)
            .setAudience("appstoreconnect-v1")
            .setExpiration(Date.from(now.plus(Duration.ofMinutes(10L))))
            .setIssuedAt(Date.from(now))
            .claim("bid", this.claim)
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact();

    }

    private Map<String, Object> getHeader() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "ES256");
        headers.put("kid", this.appleKid);
        headers.put("typ", "JWT");
        return headers;
    }

    private PrivateKey getPrivateKey() throws IOException {
        String content = readPrivateKeyFile();
        return generatePrivateKey(content);
    }

    private String readPrivateKeyFile() throws IOException {
        ClassPathResource resource = new ClassPathResource(this.appleTokenFileName);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private PrivateKey generatePrivateKey(String content) {
        String privateKey = content
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s+", "");

        try {
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Java did not support the algorithm: " + "EC", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Invalid key format", e);
        }
    }

}
