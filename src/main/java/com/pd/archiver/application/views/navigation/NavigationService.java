package com.pd.archiver.application.views.navigation;

import com.pd.archiver.application.security.UserAuthService;
import com.pd.archiver.users.domain.Roles;
import com.pd.archiver.users.entity.UserEntity;
import com.pd.archiver.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Navigation service.
 */
@Service
@RequiredArgsConstructor
public class NavigationService {
    private final UserAuthService userAuthService;
    private final UserService userDetailsService;

    /**
     * Gets user navigation.
     *
     * @return the user navigation
     */
    public List<NavigationModel> getUserNavigation() {
        if (!userAuthService.isUserLoggedIn()) {
            return Collections.emptyList();
        }

        var username = userAuthService.getCurrentlyLoggedInUser()
                .map(Authentication::getName)
                .orElse("");
        var roles = userDetailsService.findUserEntityByUsername(username)
                .map(UserEntity::getRoles).orElse(Collections.emptySet());

        List<NavigationModel> navigationModels = new ArrayList<>();

        // TODO add lists
        if (roles.contains(Roles.USER.name())) {
            navigationModels.addAll(Collections.emptyList());
        }

        if (roles.contains(Roles.ADMIN.name())) {
            navigationModels.addAll(Collections.emptyList());
        }

        return navigationModels;
    }
}