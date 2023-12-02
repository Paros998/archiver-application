package com.pd.archiver.awsfiles.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The type S 3 config.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aws-s3-config")
public class S3Config {
    private String bucketName;
    private String accessKey;
    private String secretKey;
    private String s3endpoint;
}
