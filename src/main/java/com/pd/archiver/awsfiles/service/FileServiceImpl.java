package com.pd.archiver.awsfiles.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pd.archiver.awsfiles.config.S3Config;
import com.pd.archiver.awsfiles.entity.FileEntity;
import com.pd.archiver.awsfiles.repository.FileRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The type File service.
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private static final Integer FIFTY_MB = 50_000_000;
    private static final String FILE_IS_TO_BIG = "File is to big";

    private final AmazonS3 s3client;
    private final S3Config s3Config;
    private final FileRepository fileRepository;

    private static String notFound(final UUID fileId) {
        return "File with id " + fileId + " doesn't exists in database";
    }

    private static void validateFile(MultipartFile file) {
        if (file.getSize() > FIFTY_MB) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, FILE_IS_TO_BIG);
        }
    }

    /**
     * Instantiates a new File service.
     *
     * @param s3client       the s 3 client
     * @param s3Config       the s 3 config
     * @param fileRepository the file repository
     */
    public FileServiceImpl(final AmazonS3 s3client,
                           final S3Config s3Config,
                           final FileRepository fileRepository) {
        this.s3client = s3client;
        this.s3Config = s3Config;
        this.fileRepository = fileRepository;
    }

    public Optional<FileEntity> findById(final @NonNull UUID fileId) {
        return fileRepository.findById(fileId);
    }

    public UUID initFile(final @NonNull MultipartFile file) {
        validateFile(file);

        String fileName = file.getName();

        if (!fileRepository.existsByFileName(fileName)) {
            FileEntity fileEntity = saveNewFile(file, fileName);
            return fileEntity.getFileId();
        }
        return fileRepository.getByFileName(fileName).getFileId();
    }

    public UUID uploadFile(final MultipartFile file) {
        validateFile(file);

        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        String fileName = UUID.randomUUID().toString();
        String concatFileName = fileName + "." + extension;

        if (fileRepository.existsByFileName(concatFileName))
            return fileRepository.getByFileName(concatFileName).getFileId();

        FileEntity newFile = saveNewFile(file, concatFileName);

        return newFile.getFileId();
    }

    private FileEntity saveNewFile(final MultipartFile file, final String name) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3client.putObject(s3Config.getBucketName(), name, file.getInputStream(), metadata);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

        FileEntity newFile = new FileEntity();
        newFile.setFileName(name);

        return fileRepository.save(newFile);
    }

    public String getFileUrl(final UUID fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFound(fileId))
        );

        return s3Config.getS3endpoint() + fileEntity.getFileName();
    }

    public List<String> getFilesUrls(final List<FileEntity> fileEntities) {
        return fileEntities
                .stream()
                .map(fileEntity -> s3Config.getS3endpoint() + fileEntity.getFileName())
                .toList();

    }

    public void deleteFile(final UUID fileId) {
        FileEntity fileEntity = getFileById(fileId);

        s3client.deleteObject(s3Config.getBucketName(), fileEntity.getFileName());

        fileRepository.deleteById(fileId);
    }

    public FileEntity getFileById(final UUID fileId) {
        return fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFound(fileId))
        );
    }
}