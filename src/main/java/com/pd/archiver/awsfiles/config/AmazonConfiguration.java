package com.pd.archiver.awsfiles.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The type Amazon config.
 */
@Configuration
@EntityScan("com.pd.archiver.awsfiles.entity")
@EnableJpaRepositories("com.pd.archiver.awsfiles.repository")
@ComponentScan("com.pd.archiver.awsfiles")
public class AmazonConfiguration {
    private final AWSCredentials credentials;
    private final S3Config s3Config;

    /**
     * Instantiates a new Amazon config.
     *
     * @param s3Config the s 3 config
     */
    public AmazonConfiguration(final @NonNull S3Config s3Config) {
        this.s3Config = s3Config;
        credentials = new BasicAWSCredentials(s3Config.getAccessKey(), s3Config.getSecretKey());
    }

    /**
     * S 3 client amazon s 3.
     *
     * @return the amazon s 3
     */
    @Bean
    AmazonS3 s3client() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        s3Config.getS3endpoint(), Regions.EU_CENTRAL_1.name()
                ))
                .build();
    }
}
