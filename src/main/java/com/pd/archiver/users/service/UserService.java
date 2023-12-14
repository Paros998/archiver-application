package com.pd.archiver.users.service;

import com.pd.archiver.awsfiles.api.FileDto;
import com.pd.archiver.users.api.UserDto;
import com.pd.archiver.users.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
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

    /**
     * Find user entity by username optional.
     *
     * @param username the username
     * @return the optional
     */
    Optional<UserEntity> findUserEntityByUsername(String username);

    List<FileDto> getUserFiles(UUID userId);

    UserDto getUserById(UUID userId);

    UserEntity getUserEntityById(UUID userId);
}