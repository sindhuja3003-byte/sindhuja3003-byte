package com.example.demo.auth;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthServerConfig {

    // Expose the authorization server endpoints (incl. /oauth2/token, /.well-known, /oauth2/jwks)
    @Bean
    @Order(1)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        // Allow form login if needed for consent pages (not used in client-credentials, but harmless)
        return http.formLogin(Customizer.withDefaults()).build();
    }

    // In-memory confidential client for client_credentials flow
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("my-client")
                .clientSecret("{noop}my-secret") // Basic auth with no encoding (demo)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("books.read")
                .scope("books.write")
                .build();
        return new InMemoryRegisteredClientRepository(client);
    }

    // Sign tokens with an RSA keypair generated at startup (demo). Persist in prod.
    @Bean
    public JWKSet jwkSet() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                .privateKey(kp.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
        return new JWKSet(rsaKey);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSet jwkSet) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(new ImmutableJWKSet<>(jwkSet));
    }

    // Sets the issuer used in tokens (iss claim) and discovery metadata
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }
}
