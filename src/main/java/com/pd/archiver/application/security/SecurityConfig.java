package com.pd.archiver.application.security;

import com.pd.archiver.application.security.jwt.JwtTokenFilter;
import com.pd.archiver.users.domain.Roles;
import com.pd.archiver.users.service.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
        String acao = "Access-Control-Allow-Origin";
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "https://localhost:8080", "http://localhost:3000",
                "https://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", acao, "Content-Type", "Accept",
                "Authorization", "Origin , Accept", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept",
                "Authorization", acao, acao, "Access-Control-Allow-Credentials"));
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
     * Form login authentication filter form login authentication filter.
     *
     * @param userService           the user service
     * @param authenticationManager the authentication manager
     * @param jwtSecret             the jwt secret
     * @return the form login authentication filter
     */
    @Bean
    public FormLoginAuthenticationFilter formLoginAuthenticationFilter(final UserService userService,
                                                                              final @NonNull AuthenticationManager authenticationManager,
                                                                              final @Value("${jwt.secret}") String jwtSecret) {
        var formLoginAuthenticationFilter = new FormLoginAuthenticationFilter(authenticationManager, userService, jwtSecret);
        formLoginAuthenticationFilter.setAuthenticationManager(authenticationManager);
        return formLoginAuthenticationFilter;
    }

    /**
     * Jwt token filter jwt token filter.
     *
     * @param jwtSecret the jwt secret
     * @return the jwt token filter
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter(final @Value("${jwt.secret}") String jwtSecret) {
        return new JwtTokenFilter(jwtSecret);
    }

    /**
     * Security filter chain security filter chain.
     *
     * @param http                          the http
     * @param authenticationProvider        the authentication provider
     * @param jwtTokenFilter                the jwt token filter
     * @param formLoginAuthenticationFilter the form login authentication filter
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final @NonNull HttpSecurity http,
                                                   final @NonNull AuthenticationProvider authenticationProvider,
                                                   final @NonNull JwtTokenFilter jwtTokenFilter,
                                                   final @NonNull FormLoginAuthenticationFilter formLoginAuthenticationFilter)
            throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractAuthenticationFilterConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                .addFilter(formLoginAuthenticationFilter)
                .addFilterAfter(jwtTokenFilter, FormLoginAuthenticationFilter.class)

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/actuator/**", "/actuator/health/**", "/swagger-ui/", "/swagger-ui/**",
                                "/swagger-ui.html**", "/v3/api-docs/**", "/public/**", "/favicon.ico", "/error").permitAll())

                //  API

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register", "/login").anonymous())

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/v1/files/**", "/api/v1/users/**").hasAnyRole(Roles.USER.name()))

                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        return http.build();
    }
}
