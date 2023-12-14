package com.pd.archiver.application.security;

import com.pd.archiver.users.api.LoginRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Authentication http endpoint.
 */
@RestController
public class AuthenticationHttpEndpoint {
    /**
     * Login.
     *
     * @param loginRequest the login request
     */
    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void login(final @Valid @RequestBody LoginRequest loginRequest) {
        throw new NotImplementedException("/login should not be called");
    }

}
