package com.pd.archiver.users.domain;

/**
 * The type User role not exist exception.
 */
public class UserRoleNotExistException extends RuntimeException {
    /**
     * Instantiates a new User role not exist exception.
     *
     * @param role the role
     */
    public UserRoleNotExistException(final String role) {
        super(String.format("Role %s does not exist", role));
    }
}