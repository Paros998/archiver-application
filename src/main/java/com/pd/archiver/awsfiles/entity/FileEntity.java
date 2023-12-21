package com.pd.archiver.awsfiles.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pd.archiver.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The type File entity.
 */
@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity implements Serializable {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID fileId;
    private String fileName;
    private String originalFileName;
    private Long fileSize;
    private Integer version;
    private LocalDateTime creationDate;
    private String extension;
    private Boolean backupReady;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity fileOwner;
}
