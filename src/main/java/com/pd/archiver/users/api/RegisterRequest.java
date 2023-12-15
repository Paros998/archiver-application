package com.pd.archiver.users.api;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RegisterRequest {
    @NonNull
    private String username;

    @NonNull
    private String password;
}