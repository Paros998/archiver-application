package com.pd.archiver.users.service;

import com.pd.archiver.users.api.UserDto;
import com.pd.archiver.users.domain.Roles;
import com.pd.archiver.users.domain.UserRoleNotExistException;
import com.pd.archiver.users.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * The type User converter.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConverter {
    /**
     * To user dto user dto.
     *
     * @param user the user
     * @return the user dto
     */
    public static UserDto toUserDto(final UserEntity user) {
        return UserDto.builder()
                .id(String.valueOf(user.getId()))
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .roles(user.getRoles())
                .build();
    }

    /**
     * To user user.
     *
     * @param userDto the user dto
     * @return the user
     */
    public static UserEntity toUserEntity(final UserDto userDto) {
        List<String> roles = Arrays.stream(Roles.values()).map(Enum::name).toList();

        userDto.getRoles().forEach(role -> {
            if (!roles.contains(role)) throw new UserRoleNotExistException(role);
        });

        return new UserEntity(userDto.getUsername(), userDto.getPassword(), true, userDto.getRoles());
    }
}