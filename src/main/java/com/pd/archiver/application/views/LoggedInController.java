package com.pd.archiver.application.views;

import com.pd.archiver.application.security.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The type Logged in controller.
 */
@Controller
@RequiredArgsConstructor
public class LoggedInController {
    private final UserAuthService userAuthService;

    /**
     * Default render string.
     *
     * @return the string
     */
    @RequestMapping(value = "/afterLogin", method = {RequestMethod.POST, RequestMethod.GET})
    public String defaultRender() {
        if (!userAuthService.isUserLoggedIn()) {
            return "/login";
        }

        return "main";
    }
}