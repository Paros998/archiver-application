package com.pd.archiver.awsfiles.repository;

import com.pd.archiver.awsfiles.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;


/**
 * The interface File repository.
 */
public interface FileRepository extends JpaRepository<FileEntity, UUID>  {
    /**
     * Exists by file name boolean.
     *
     * @param fileName the file name
     * @return the boolean
     */
    boolean existsByFileName(String fileName);

    /**
     * Gets by file name.
     *
     * @param fileName the file name
     * @return the by file name
     */
    FileEntity getByFileName(String fileName);

    boolean existsByOriginalFileName(String originalFileName);

    @Query("SELECT count(f.version) FROM FileEntity f WHERE f.originalFileName = :originalFileName")
    Integer findMaxVersionByOriginalFileName(@Param("originalFileName") String originalFileName);

}



