package com.floney.floney.user.client.util;

import com.floney.floney.common.exception.user.OAuthTokenNotValidException;
import com.floney.floney.user.client.dto.ApplePublicKey;
import com.floney.floney.user.client.dto.AppleTokenHeader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
public class AppleOAuthPublicKeyGenerator {

    private static final int POSITIVE_SIGNUM = 1;

    public PublicKey generate(final AppleTokenHeader OAuthTokenHeader, final List<ApplePublicKey> publicKeys) {
        final ApplePublicKey publicKey = findKeyByHeader(OAuthTokenHeader, publicKeys);

        return generatePublicKeyWithApplePublicKey(publicKey);
    }

    private ApplePublicKey findKeyByHeader(final AppleTokenHeader OAuthTokenHeader,
                                           final List<ApplePublicKey> publicKeys) {
        return publicKeys.stream()
                .filter(key -> Objects.equals(key.getAlg(), OAuthTokenHeader.getAlg()))
                .filter(key -> Objects.equals(key.getKid(), OAuthTokenHeader.getKid()))
                .findAny()
                .orElseThrow(OAuthTokenNotValidException::new);
    }

    private PublicKey generatePublicKeyWithApplePublicKey(final ApplePublicKey applePublicKey) {
        final byte[] n = Base64Utils.decodeFromUrlSafeString(applePublicKey.getN());
        final byte[] e = Base64Utils.decodeFromUrlSafeString(applePublicKey.getE());

        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                new BigInteger(POSITIVE_SIGNUM, n), new BigInteger(POSITIVE_SIGNUM, e)
        );

        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.getKty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new OAuthTokenNotValidException();
        }
    }
}
