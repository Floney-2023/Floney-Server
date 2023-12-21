package com.floney.floney.infra.googleAlarm.client;

import com.floney.floney.common.exception.alarm.GoogleAccessTokenGenerateException;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GoogleGoogleTokenProvider implements GoogleTokenProxy {

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String GOOGLE_CREDENTIAL_FILE_NAME = "secrets/google-credential.json";

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Override
    public String generate() {
        try {
            final GoogleCredentials googleCredentials = getGoogleCredentials();

            logger.info("구글 access token 발급 요청");
            return googleCredentials.refreshAccessToken().getTokenValue();
        } catch (final IOException exception) {
            logger.error(exception.getLocalizedMessage());
            throw new GoogleAccessTokenGenerateException();
        }
    }

    private static GoogleCredentials getGoogleCredentials() throws IOException {
        final ClassPathResource jsonResource = new ClassPathResource(GOOGLE_CREDENTIAL_FILE_NAME);
        return GoogleCredentials
                .fromStream(jsonResource.getInputStream())
                .createScoped(MESSAGING_SCOPE);
    }
}
