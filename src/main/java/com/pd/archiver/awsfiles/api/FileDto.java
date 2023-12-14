package com.pd.archiver.awsfiles.api;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.pd.archiver.awsfiles.entity.FileEntity}
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FileDto implements Serializable {
    UUID fileId;
    String fileName;
    String originalFileName;
    Long fileSize;
    Integer version;
    LocalDateTime creationDate;
    String extension;
    Boolean backupReady;
}