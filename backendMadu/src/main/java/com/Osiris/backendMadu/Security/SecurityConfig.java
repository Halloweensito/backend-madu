package com.Osiris.backendMadu.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    // ----------------------------
    // Seguridad HTTP principal
    // ----------------------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas admin protegidas
                        .requestMatchers("/api/admin/pages/**").authenticated() // <--- NUEVO

                        .requestMatchers("/api/home/admin").authenticated()
                        .requestMatchers("/api/site-settings/admin/**").authenticated()
                        .requestMatchers("/api/admin/footer-sections/**").authenticated()
                        .requestMatchers("/api/admin/footer-links/**").authenticated()
                        .requestMatchers("/api/admin/orders/**").authenticated()

                        // Rutas públicas
                        .requestMatchers(HttpMethod.POST, "/api/public/orders").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/pages/**").permitAll() // <--- NUEVO
                        .requestMatchers(HttpMethod.GET, "/api/products/store/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/{slug}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/store/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/slug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/home/store").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/site-settings/store").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/footer/**").permitAll()
                        .requestMatchers("/api/products/admin/**").authenticated()
                        .requestMatchers("/api/categories/admin/**").authenticated()
                        .requestMatchers("/actuator/health").permitAll()

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    // ----------------------------
    // CORS
    // ----------------------------
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ----------------------------
    // Roles desde app_metadata.roles
    // ----------------------------
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("app_metadata.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.ES256)
                .build();
    }
}
