package com.pd.archiver.users.controller;

import com.pd.archiver.awsfiles.api.FileDto;
import com.pd.archiver.users.api.RegisterRequest;
import com.pd.archiver.users.api.UserDto;
import com.pd.archiver.users.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/users")
@AllArgsConstructor
@Tag(name = "Users")
public class UsersHttpEndpoint {
    private final UserService userService;

    @GetMapping("{userId}/files")
    public List<FileDto> getUserFiles(final @NonNull @PathVariable UUID userId) {
        return userService.getUserFiles(userId);
    }

    @GetMapping("{userId}/files/last")
    public List<FileDto> getLasUserFiles(final @NonNull @PathVariable UUID userId,
                                         final @RequestParam(required = false, defaultValue = "3") int limit) {
        return userService.getLastUserFiles(userId, limit);
    }

    @GetMapping("{userId}")
    public UserDto getUserById(final @PathVariable @NonNull UUID userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("register")
    public UUID registerUser(final @NonNull @RequestBody RegisterRequest request) {
        return userService.registerNewUser(request.getUsername(), request.getPassword());
    }
}
