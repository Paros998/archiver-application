package com.pd.archiver.awsfiles.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pd.archiver.application.security.UserAuthService;
import com.pd.archiver.awsfiles.api.FileUrls;
import com.pd.archiver.awsfiles.config.S3Config;
import com.pd.archiver.awsfiles.entity.FileEntity;
import com.pd.archiver.awsfiles.repository.FileRepository;
import com.pd.archiver.users.entity.UserEntity;
import com.pd.archiver.users.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * The type File service.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private static final Integer FILE_NAME_INDEX = 0;
    private static final Integer EXTENSION_INDEX = 1;
    private static final Integer FIFTY_MB = 52_428_800;
    private static final String FILE_IS_TO_BIG = "File is to big";
    private static final String FILE_FORMAT = "%s.%s";

    private final AmazonS3 s3client;
    private final S3Config s3Config;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final UserAuthService authService;
    private final Executor asyncExecutor;

    private static String notFound(final UUID fileId) {
        return "File with id " + fileId + " doesn't exists in database";
    }

    private static void validateFile(MultipartFile file) {
        if (file.getSize() > FIFTY_MB) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, FILE_IS_TO_BIG);
        }
    }

    public Optional<FileEntity> findById(final @NonNull UUID fileId) {
        return fileRepository.findById(fileId);
    }

    @Override
    @Transactional
    public UUID initFile(final @NonNull MultipartFile file) {
        validateFile(file);

        String[] fileData = getFileData(Objects.requireNonNull(file.getOriginalFilename()));

        String fileName = file.getName();

        if (!fileRepository.existsByFileName(fileName)) {
            FileEntity fileEntity = saveNewFile(file, fileName, fileData[FILE_NAME_INDEX], fileData[EXTENSION_INDEX]);
            return fileEntity.getFileId();
        }
        return fileRepository.getByFileName(fileName).getFileId();
    }

    @Override
    @Transactional
    public UUID uploadFile(final MultipartFile file) {
        validateFile(file);

        String[] fileData = getFileData(Objects.requireNonNull(file.getOriginalFilename()));

        String extension = fileData[EXTENSION_INDEX];
        String fileName = UUID.randomUUID().toString();
        String newFileName = String.format(FILE_FORMAT, fileName, extension);

        if (fileRepository.existsByFileName(newFileName))
            return fileRepository.getByFileName(newFileName).getFileId();

        FileEntity newFile = saveNewFile(file, newFileName, fileData[FILE_NAME_INDEX], extension);

        return newFile.getFileId();
    }

    @Override
    public FileUrls getFileUrls(final UUID fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFound(fileId))
        );

        return toFileUrls(fileEntity);
    }

    @Override
    public List<FileUrls> getFilesUrls(final List<FileEntity> fileEntities) {
        return fileEntities
                .stream()
                .map(this::toFileUrls)
                .toList();

    }

    private FileUrls toFileUrls(final FileEntity fileEntity) {
        String urlFormat = s3Config.getS3endpoint() + "/%s/" + fileEntity.getFileName();

        return FileUrls.builder()
                .fileUrl(String.format(urlFormat, s3Config.getBucketName()))
                .backupFileUrl(fileEntity.getBackupReady() ? String.format(urlFormat, s3Config.getBackupBucketName()) : null)
                .build();
    }

    @Override
    @Transactional
    public void deleteFile(final UUID fileId) {
        FileEntity fileEntity = getFileById(fileId);

        s3client.deleteObject(s3Config.getBucketName(), fileEntity.getFileName());

        asyncExecutor.execute(() -> {
            try {
                s3client.deleteObject(s3Config.getBackupBucketName(), fileEntity.getFileName());
            } catch (final SdkClientException e) {
                log.error("Error occurred while clearing file from backup, details: {}", e.getLocalizedMessage());
            }
        });

        fileRepository.deleteById(fileId);
    }

    @Override
    @Transactional
    public void updateFileName(final UUID fileId, final String newName) {
        // todo check for permission to change file name, ADMIN = true, USER = true if fileEntity owner is current user

        FileEntity fileEntity = getFileById(fileId);
        String oldName = fileEntity.getFileName();
        String fullNewName = String.format(FILE_FORMAT, newName, fileEntity.getExtension());
        fileEntity.setFileName(fullNewName);

        fileRepository.saveAndFlush(fileEntity);

        s3client.copyObject(s3Config.getBucketName(), oldName, s3Config.getBucketName(), fullNewName);

        asyncExecutor.execute(() -> {
            try {
                s3client.deleteObject(s3Config.getBucketName(), oldName);
                updateBackupStatus(fileEntity.getFileId(), false);
                s3client.copyObject(s3Config.getBucketName(), fullNewName, s3Config.getBackupBucketName(), fullNewName);
                updateBackupStatus(fileEntity.getFileId(), true);
                s3client.deleteObject(s3Config.getBackupBucketName(), oldName);
            } catch (final SdkClientException e) {
                log.error("Error occurred while clearing file from backup, details: {}", e.getLocalizedMessage());
            }
        });
    }

    @Override
    public FileEntity getFileById(final UUID fileId) {
        return fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFound(fileId))
        );
    }

    private String[] getFileData(final String originalFileName) {
        return Objects.requireNonNull(originalFileName).split("\\.");
    }

    private FileEntity saveNewFile(final MultipartFile file, final String name, final String originalName, final String extension) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3client.putObject(s3Config.getBucketName(), name, file.getInputStream(), metadata);
            log.info("File {} has been saved to {} bucket, initiating backup.", file.getOriginalFilename(), s3Config.getBucketName());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

        final var user = authService.getCurrentlyLoggedInUser();

        final UserEntity userEntity = user
                .flatMap(authentication -> userService.findUserEntityByUsername(authentication.getName())).orElse(null);

        final FileEntity newFile = FileEntity.builder()
                .fileName(name)
                .originalFileName(originalName)
                .fileOwner(userEntity)
                .creationDate(LocalDateTime.now())
                .fileSize(file.getSize()) // in bytes
                .extension(extension)
                .version(1) // new file always version 1
                .build();

        fileRepository.save(newFile);

        asyncExecutor.execute(() -> {
            log.info("Backup of file {} started", name);
            try {
                s3client.copyObject(s3Config.getBucketName(), name, s3Config.getBackupBucketName(), name);
            } catch (final SdkClientException e) {
                log.error("An error occurred while creating a backup, details: {}", e.getLocalizedMessage());
            }
            updateBackupStatus(newFile.getFileId(), true);
        });

        return newFile;
    }

    private void updateBackupStatus(final UUID fileId, final boolean isReady) {
        Optional<FileEntity> fileBox = fileRepository.findById(fileId);

        if (fileBox.isEmpty()) {
            log.error("Couldn't find file by id: {}", fileId);
            return;
        }

        FileEntity file = fileBox.get();
        file.setBackupReady(isReady);
        fileRepository.save(file);
        if (isReady) {
            log.info("Backup of file {} finished", file.getFileName());
        } else log.info("Backup of file {} revoked", file.getFileName());
    }
}