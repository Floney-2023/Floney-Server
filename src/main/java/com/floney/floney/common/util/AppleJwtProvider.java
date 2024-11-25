package com.floney.floney.common.util;

import com.apple.itunes.storekit.model.Environment;
import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.apple.itunes.storekit.model.ResponseBodyV2DecodedPayload;
import com.apple.itunes.storekit.verification.SignedDataVerifier;
import com.apple.itunes.storekit.verification.VerificationException;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppleJwtProvider {
    private final String appleKid;
    private final String appleTokenFileName;
    private String issuer;
    private String claim;
    private Long appleId;

    private String env;

    public AppleJwtProvider(
        @Value("${apple.kid}") String appleKid,
        @Value("${apple.tokenFileName}") String tokenFileName,
        @Value("${apple.issuer}") String issuer,
        @Value("${apple.bundle-id}") String claim,
        @Value("${apple.app-apple-id}") String appleId,
        @Value("${apple.env}") String env
    ) {
        this.appleKid = appleKid;
        this.appleTokenFileName = "secrets/" + tokenFileName;
        this.issuer = issuer;
        this.claim = claim;
        this.appleId = Long.parseLong(appleId);
        this.env = env;
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

    public JWSTransactionDecodedPayload parseTransaction(String notificationPayload) throws IOException, VerificationException {
        String bundleId = this.claim;
        ClassPathResource resource = new ClassPathResource("/secrets/AppleRootCA-G2.cer");
        ClassPathResource resource2 = new ClassPathResource("/secrets/AppleRootCA-G3.cer");

        Set<InputStream> rootCAs = Set.of(
            resource.getInputStream(),
            resource2.getInputStream()
        );

        SignedDataVerifier signedPayloadVerifier = new SignedDataVerifier(rootCAs, bundleId, this.appleId, Environment.fromValue(this.env), true);

        try {
            return signedPayloadVerifier.verifyAndDecodeTransaction(notificationPayload);
        } catch (VerificationException e) {
            throw e;
        }
    }

    public ResponseBodyV2DecodedPayload parseNotification(String notificationPayload) throws IOException, VerificationException {
        String bundleId = this.claim;
        ClassPathResource resource = new ClassPathResource("/secrets/AppleRootCA-G2.cer");
        ClassPathResource resource2 = new ClassPathResource("/secrets/AppleRootCA-G3.cer");

        Set<InputStream> rootCAs = Set.of(
            resource.getInputStream(),
            resource2.getInputStream()
        );


        System.out.println("bundleId: " + bundleId);
        System.out.println("appleId: " + this.appleId);
        System.out.println("env: " + Environment.fromValue(this.env));

        SignedDataVerifier signedPayloadVerifier = new SignedDataVerifier(rootCAs, bundleId, this.appleId, Environment.fromValue(this.env), true);

        try {

            return signedPayloadVerifier.verifyAndDecodeNotification(notificationPayload);
        } catch (VerificationException e) {
            throw e;
        }
    }


}
