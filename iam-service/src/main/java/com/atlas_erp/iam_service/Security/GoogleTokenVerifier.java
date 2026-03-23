package com.atlas_erp.iam_service.Security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    // Chỉ dùng DUY NHẤT một constructor này, bỏ @RequiredArgsConstructor đi
    public GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public GoogleTokenPayload verify(String credential) {
        try {
            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken == null) {
                throw new IllegalArgumentException("Invalid Google credential");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleId = payload.getSubject(); // 'sub' field
            String email = payload.getEmail();

            if (googleId == null || googleId.isBlank() || email == null || email.isBlank()) {
                throw new IllegalArgumentException("Invalid token payload");
            }

            return new GoogleTokenPayload(googleId, email);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Google credential: " + e.getMessage(), e);
        }
    }

    public record GoogleTokenPayload(String googleId, String email) {
    }
}