package com.pd.archiver.users.api;

import lombok.*;

/**
 * The type Login request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class LoginRequest {
    @NonNull
    private String username;

    @NonNull
    private String password;
}