package org.example.greetingright.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disables CSRF protection because we use JWT (stateless) and not cookies
                .csrf(csrf -> csrf.disable())
                // Disables the default Spring login form
                .formLogin(form -> form.disable())
                // Configures CORS to allow requests from the frontend (React app on localhost:3000)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    // Only allow requests from this origin (the frontend)
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    // Allow frontend HTTP methods, including OPTIONS used by the browser to check permission before real requests (CORS)
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    // Allow all headers in requests
                    config.setAllowedHeaders(List.of("*"));
                    // Allow credentials (like cookies or authorization headers)
                    config.setAllowCredentials(true);
                    return config;
                }))
                // server does NOT keep user session, so every request must include authentication info (like using JWT token)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/signup",
                                "/refresh-token",
                                "/logout"
                        ).permitAll()
                        .requestMatchers("/api/auth/users").hasAnyRole( "ADMIN")
                        .requestMatchers("/api/auth/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                // Disables the default Spring logout
                .logout(logout -> logout.disable())
                // Adds a custom filter to check JWT tokens before the standard username/password filter
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );
        // Builds and returns the security filter chain
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
