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

    // 1. INYECCIÓN DE LA VARIABLE DESDE PROPERTIES
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 2. REGLAS DE SEGURIDAD (Orden Crítico)

                        // A. PRIMERO: Protege explícitamente las rutas de administración
                        // Si no haces esto, /{slug} podría dejar ver /admin
                        .requestMatchers("/api/products/admin/**").authenticated()
                        .requestMatchers("/api/categories/admin/**").authenticated()

                        // B. SEGUNDO: Rutas Públicas Específicas
                        .requestMatchers(HttpMethod.GET, "/api/products/store/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/category/**").permitAll()

                        // C. TERCERO: Comodines Públicos (Slugs)
                        // Esto debe ir DESPUÉS de proteger /admin
                        .requestMatchers(HttpMethod.GET, "/api/products/{slug}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/store/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/slug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/home/store").permitAll()

                        // E. Actuator Health para Render
                        .requestMatchers("/actuator/health").permitAll()

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Aquí usamos la variable inyectada arriba
        config.setAllowedOrigins(allowedOrigins);

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtConverter;
    }
}