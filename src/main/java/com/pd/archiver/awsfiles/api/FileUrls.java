package com.pd.archiver.awsfiles.api;

import lombok.*;

import java.io.Serializable;

/**
 * The type File urls.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FileUrls implements Serializable {
    private String fileUrl;
    private String backupFileUrl;
}