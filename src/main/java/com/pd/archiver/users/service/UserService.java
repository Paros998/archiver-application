package com.pd.archiver.users.service;

import com.pd.archiver.users.api.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

/**
 * The interface User service.
 */
public interface UserService extends UserDetailsService {

    /**
     * Create user string.
     *
     * @param createDto the creation dto
     * @return the string
     */
    UUID createUser(UserDto createDto);

    /**
     * Gets all users.
     *
     * @return the all users
     */
    List<UserDto> getAllUsers();

}