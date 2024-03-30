package com.geopokrovskiy.security;

import com.geopokrovskiy.exception.AuthException;
import com.geopokrovskiy.exception.ErrorCodes;
import com.geopokrovskiy.repository.MerchantRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
@Data
public class CredentialsVerifier {
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<String> verifyCredentials(String authorizationHeader) {
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] usernamePassword = credentials.split(":", 2);

        String username = usernamePassword[0];
        String password = usernamePassword[1];

        return this.merchantRepository.findByUsername(username)
                .flatMap(m -> {
                    if (!passwordEncoder.encode(password).equals(m.getPassword()) || !username.equals(m.getUsername())) {
                        return Mono.error(new AuthException("Invalid credentials!", ErrorCodes.INVALID_CREDENTIALS));
                    }
                    return Mono.just(username);
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid credentials!", ErrorCodes.INVALID_CREDENTIALS)));
    }
}
