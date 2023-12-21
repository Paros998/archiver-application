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

    /**
     * Gets user files.
     *
     * @param userId the user id
     * @return the user files
     */
    List<FileDto> getUserFiles(UUID userId);

    /**
     * Gets user by id.
     *
     * @param userId the user id
     * @return the user by id
     */
    UserDto getUserById(UUID userId);

    /**
     * Gets user entity by id.
     *
     * @param userId the user id
     * @return the user entity by id
     */
    UserEntity getUserEntityById(UUID userId);

    /**
     * Register new user uuid.
     *
     * @param username the username
     * @param password the password
     * @return the uuid
     */
    UUID registerNewUser(String username, String password);

    /**
     * Gets last user files.
     *
     * @param userId the user id
     * @param limit  the limit
     * @return the last user files
     */
    List<FileDto> getLastUserFiles(UUID userId, int limit);
}