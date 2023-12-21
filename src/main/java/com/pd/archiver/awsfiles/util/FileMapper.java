package com.pd.archiver.awsfiles.util;

import com.pd.archiver.awsfiles.api.FileDto;
import com.pd.archiver.awsfiles.entity.FileEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * The type File mapper.
 */
@UtilityClass
public class FileMapper {
    /**
     * To file dto list.
     *
     * @param entities the entities
     * @return the list
     */
    public List<FileDto> toFileDtoList(final @NonNull List<FileEntity> entities) {
        return entities.stream()
                .map(FileMapper::toFileDto)
                .toList();
    }

    /**
     * To file dto.
     *
     * @param entity the entity
     * @return the file dto
     */
    public FileDto toFileDto(final @NonNull FileEntity entity) {
        return FileDto.builder()
                .fileId(entity.getFileId())
                .fileSize(entity.getFileSize())
                .fileName(entity.getFileName())
                .originalFileName(entity.getOriginalFileName())
                .extension(entity.getExtension())
                .version(entity.getVersion())
                .backupReady(entity.getBackupReady())
                .creationDate(entity.getCreationDate())
                .build();
    }
}