package com.pd.archiver.awsfiles.controller;
import com.pd.archiver.awsfiles.api.FileDto;
import com.pd.archiver.awsfiles.api.FileUrls;
import com.pd.archiver.awsfiles.api.NameValidationRequest;
import com.pd.archiver.awsfiles.entity.FileEntity;
import com.pd.archiver.awsfiles.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * The type File controller.
 */
@RestController
@RequestMapping(path = "api/v1/files")
@AllArgsConstructor
@Tag(name = "Files")
public class FilesController {
    private final FileService fileService;

    /**
     * TEST.
     *
     * @param fileId the file id
     * @return the file
     */
    @GetMapping("{fileId}/test")
    public FileDto getFileTest(final @NonNull @PathVariable UUID fileId) {
        return fileService.getFileByIdTest(fileId);
    }
    /**
     * Gets file.
     *
     * @param fileId the file id
     * @return the file
     */
    @GetMapping("{fileId}/info")
    public FileEntity getFile(final @NonNull @PathVariable UUID fileId) {
        return fileService.getFileById(fileId);
    }
    /**
     * Gets file url.
     *
     * @param fileId the file id
     * @return the file url
     */
    @GetMapping("{fileId}")
    public FileUrls getFileUrl(final @NonNull @PathVariable UUID fileId) {
        return fileService.getFileUrls(fileId);
    }

    /**
     * Check name existence boolean.
     *
     * @param request the request
     * @return the boolean
     */
    @PostMapping("name-check")
    public Boolean checkNameExistence(final @NonNull @RequestBody NameValidationRequest request) {
        return fileService.checkNewNameExistence(request.getName(), request.getExtension());
    }

    /**
     * Upload file uuid.
     *
     * @param file the file
     * @return the uuid
     */
    @PostMapping(value = "/rest/upload", consumes = {
            "multipart/form-data"})
    public UUID uploadFile(final @NonNull @RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    /**
     * Delete file.
     *
     * @param fileId the file id
     */
    @DeleteMapping("{fileId}")
    public void deleteFile(final @NonNull @PathVariable UUID fileId) {
        fileService.deleteFile(fileId);
    }

    /**
     * Update file name.
     *
     * @param fileId  the file id
     * @param newName the new name
     */
    @PutMapping("{fileId}/name/{newName}")
    public void updateFileName(final @NonNull @PathVariable UUID fileId, final @NonNull @PathVariable String newName) {
        fileService.updateFileName(fileId, newName);
    }
}
