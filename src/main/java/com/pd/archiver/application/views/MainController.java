package com.pd.archiver.application.views;

import com.pd.archiver.application.security.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The type Main controller.
 */
@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserAuthService userAuthService;

    /**
     * Default render string.
     *
     * @return the string
     */
    @GetMapping("/")
    public String defaultRender() {
        if (!userAuthService.isUserLoggedIn()) {
            return "/login";
        }

        return "main";
    }

    /**
     * Render on login string.
     *
     * @return the string
     */
    @GetMapping("/login")
    public String renderOnLogin() {
        return "/login";
    }

    /**
     * Render on sign up string.
     *
     * @return the string
     */
    @GetMapping("/signUp")
    public String renderOnSignUp() {
        return "/sign-up";
    }
}