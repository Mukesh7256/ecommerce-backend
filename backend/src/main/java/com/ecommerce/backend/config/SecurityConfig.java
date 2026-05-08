package com.ecommerce.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // No sessions - use JWT only
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC - Register & Login
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ PUBLIC - Anyone can VIEW products
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // ✅ PUBLIC - Anyone can VIEW product by category
                        .requestMatchers(HttpMethod.GET, "/api/products/category/**").permitAll()

                        // Cart - needs login
                        .requestMatchers("/api/cart/**").authenticated()

                        // Orders - needs login
                        .requestMatchers("/api/orders/**").authenticated()

                        // 🔒 SECURED - Only logged in users can ADD products
                        .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()

                        // 🔒 SECURED - Only logged in users can UPDATE products
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()

                        // 🔒 SECURED - Only logged in users can DELETE products
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()

                        // 🔒 SECURED - User profile needs token
                        .requestMatchers("/api/user/**").authenticated()

                        // 🔒 Everything else needs JWT
                        .anyRequest().authenticated()
                )

                // Add JWT filter before Spring Security filter
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow React frontend
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // Allow all HTTP methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Allow all headers including Authorization
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}