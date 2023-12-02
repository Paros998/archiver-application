package com.pd.archiver.users.runners;

import com.pd.archiver.users.api.UserDto;
import com.pd.archiver.users.domain.Roles;
import com.pd.archiver.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * The type Users local runner.
 */
@Service
@Log4j2
@RequiredArgsConstructor
@Profile("devlocal")
public class UsersLocalRunner implements ApplicationRunner {
    private final UserService userService;

    @Override
    public void run(final ApplicationArguments args) {
        log.debug("{} running", this.getClass().getSimpleName());
        val user = UserDto.builder()
                .username("user")
                .password("user")
                .roles(Set.of(Roles.USER.name()))
                .build();
        val moderator = UserDto.builder()
                .username("moderator")
                .password("moderator")
                .roles(Set.of(Roles.MODERATOR.name()))
                .build();
        val admin = UserDto.builder()
                .username("admin")
                .password("admin")
                .roles(Set.of(Roles.ADMIN.name()))
                .build();
        userService.createUser(user);
        userService.createUser(moderator);
        userService.createUser(admin);
    }
}