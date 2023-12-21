package com.pd.archiver.application.security;

import com.pd.archiver.application.security.jwt.JwtExpire;
import com.pd.archiver.users.entity.UserEntity;
import com.pd.archiver.users.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * The type Form login authentication filter.
 */
@Getter
@AllArgsConstructor
public class FormLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final boolean POST_ONLY = true;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final String secretKey;

    @Override
    public Authentication attemptAuthentication(final @NonNull HttpServletRequest request, final @NonNull HttpServletResponse response) throws AuthenticationException {

        if (POST_ONLY && !request.getMethod().equals("POST"))
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());

        String username = this.obtainUsername(request);
        username = username != null ? username : "";
        username = username.trim();

        String password = this.obtainPassword(request);
        password = password != null ? password : "";

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        this.setDetails(request, authRequest);

        return authenticationManager.authenticate(authRequest);

    }

    @Override
    protected void successfulAuthentication(final @NonNull HttpServletRequest request, final @NonNull HttpServletResponse response, final @NonNull FilterChain chain,
                                            final @NonNull Authentication authentication) throws IOException, ServletException {
        try {

            String username = this.obtainUsername(request);

            final UserEntity user = userService.findUserEntityByUsername(username)
                    .orElseThrow();

            String accessToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("authorities", authentication.getAuthorities())
                    .claim("userId", user.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + JwtExpire.ACCESS_TOKEN.getAmount()))
                    .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .compact();


            response.addHeader("Authorization", "Bearer " + accessToken);
        } catch (final Exception e) {
            if (e.equals(new IOException(e.getMessage())))
                throw new IOException(e.getMessage());

            else throw new ServletException(e.getMessage());
        }
    }
}