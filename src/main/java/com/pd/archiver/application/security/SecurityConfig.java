package com.pd.archiver.application.security;

import com.pd.archiver.users.domain.Roles;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * The type Security config.
 */
@Configuration
public class SecurityConfig {

    /**
     * B crypt password encoder b crypt password encoder.
     *
     * @return the b crypt password encoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    /**
     * Cors configuration source cors configuration source.
     *
     * @return the cors configuration source
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "https://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Role hierarchy.
     *
     * @return the role hierarchy
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String format = "ROLE_%s > ROLE_%s";

        List<String> hierarchies = List.of(
                String.format(format, Roles.ADMIN.name(), Roles.USER.name()),
                String.format(format, Roles.ADMIN.name(), Roles.MODERATOR.name()),
                String.format(format, Roles.MODERATOR.name(), Roles.USER.name())
        );

        var finalHierarchy = hierarchies.stream().reduce((s, s2) -> s.concat("\n" + s2)).orElse("");

        roleHierarchy.setHierarchy(finalHierarchy);
        return roleHierarchy;
    }

    /**
     * Method security expression handler method security expression handler.
     *
     * @param roleHierarchy the role hierarchy
     * @return the method security expression handler
     */
    @Bean
    static @NonNull MethodSecurityExpressionHandler methodSecurityExpressionHandler(final @NonNull RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    /**
     * Security filter chain security filter chain.
     *
     * @param http                   the http
     * @param authenticationProvider the authentication provider
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final @NonNull HttpSecurity http,
                                                   final @NonNull AuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/actuator/**", "/actuator/health/**", "/swagger-ui/", "/swagger-ui/**",
                                "/swagger-ui.html**", "/v3/api-docs/**", "/public/**", "/favicon.ico").permitAll())

                //  API

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/v1/files/**").hasAnyRole(Roles.USER.name()))

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/v1/users/**").hasAnyRole(Roles.ADMIN.name()))

                // VIEWS

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/login", "/signUp").anonymous())

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/logout").fullyAuthenticated())

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/main", "/myFiles").hasAnyRole(Roles.USER.name()))

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/users").hasAnyRole(Roles.ADMIN.name()))

//                .authorizeHttpRequests(requests -> requests.requestMatchers("/h2-console/**").permitAll())

                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .successForwardUrl("/")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
        ;

        return http.build();
    }
}
