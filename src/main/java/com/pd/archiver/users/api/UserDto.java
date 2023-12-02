package com.pd.archiver.users.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Set;

/**
 * The type User dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class UserDto {
    private String id;
    private String username;

    @JsonIgnore
    private String password;
    private boolean enabled;
    private Set<String> roles;
}