package com.pd.archiver.users.service;

import com.pd.archiver.awsfiles.api.FileDto;
import com.pd.archiver.awsfiles.util.FileMapper;
import com.pd.archiver.users.api.UserDto;
import com.pd.archiver.users.entity.UserEntity;
import com.pd.archiver.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Default user service.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<UserEntity> userBox = userDao.findByUsername(username);
        if (userBox.isEmpty()) {
            throw new UsernameNotFoundException("The user with the given login does not exist:" + username);
        }

        var user = userBox.get();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }

    @Override
    @Transactional
    public UUID createUser(final UserDto createDto) {
        log.info("Starting processing user creation request: {}", createDto);

        val user = UserConverter.toUserEntity(createDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userDao.save(user).getId();
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream()
                .map(UserConverter::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public Optional<UserEntity> findUserEntityByUsername(final String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional
    public List<FileDto> getUserFiles(final UUID userId) {
        val user = fetchUserById(userId);
        return FileMapper.toFileDtoList(user.getUserFiles());
    }

    @Override
    @Transactional
    public UserDto getUserById(final UUID userId) {
        return UserConverter.toUserDto(fetchUserById(userId));
    }

    @Override
    @Transactional
    public UserEntity getUserEntityById(final UUID userId) {
        return fetchUserById(userId);
    }

    private UserEntity fetchUserById(final UUID userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id: [%s] not found", userId)));
    }

}
