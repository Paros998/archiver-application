package com.pd.archiver.users.repository;

import com.pd.archiver.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * The interface User repository.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    /**
     * Exists by username boolean.
     *
     * @param username the username
     * @return the boolean
     */
    boolean existsByUsername(String username);

    /**
     * Find by username optional.
     *
     * @param username the username
     * @return the optional
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Gets by username.
     *
     * @param username the username
     * @return the by username
     */
    UserEntity getByUsername(String username);
}
