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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * The type Amazon config.
 */
@EnableAsync
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

    /**
     * Async executor executor.
     *
     * @return the executor
     */
    @Bean
    @Primary
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("ArchiverBackupRunner-");
        executor.initialize();
        return executor;
    }
}
