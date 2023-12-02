package com.pd.archiver.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * The type User entity.
 */
@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String password;
    private boolean enabled;

    @ElementCollection(targetClass = String.class)
    private Set<String> roles;

    /**
     * Instantiates a new User entity.
     *
     * @param username the username
     * @param password the password
     * @param enabled  the enabled
     * @param roles    the roles
     */
    public UserEntity(final String username,final String password, boolean enabled,final Set<String> roles) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
    }

}
