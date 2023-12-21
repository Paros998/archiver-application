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
    /**
     * The File id.
     */
    UUID fileId;
    /**
     * The File name.
     */
    String fileName;
    /**
     * The Original file name.
     */
    String originalFileName;
    /**
     * The File size.
     */
    Long fileSize;
    /**
     * The Version.
     */
    Integer version;
    /**
     * The Creation date.
     */
    LocalDateTime creationDate;
    /**
     * The Extension.
     */
    String extension;
    /**
     * The Backup ready.
     */
    Boolean backupReady;
}