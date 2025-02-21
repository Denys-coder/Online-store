package Onlinestore.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for REST APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/about", "/error", "/catalog/**").permitAll()
                        .requestMatchers("/profile/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Allow session creation
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // Enable logout on /auth/logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                            response.getWriter().flush();
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID") // Ensure session cookie is removed
                )
                .formLogin(AbstractHttpConfigurer::disable) // Disable default form-based login
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
