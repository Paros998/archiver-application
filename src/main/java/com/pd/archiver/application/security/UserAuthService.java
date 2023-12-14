package com.pd.archiver.application.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The type User auth service.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserAuthService {
    /**
     * Gets currently logged-in user.
     *
     * @return the currently logged-in user
     */
    public Optional<Authentication> getCurrentlyLoggedInUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return Optional.of(auth);
        }

        return Optional.empty();
    }

    /**
     * Is user logged in boolean.
     *
     * @return the boolean
     */
    public boolean isUserLoggedIn() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return !(auth instanceof AnonymousAuthenticationToken);
    }
}
