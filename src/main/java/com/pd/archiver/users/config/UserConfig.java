package com.pd.archiver.users.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The type User config.
 */
@Configuration
@EntityScan("com.pd.archiver.users.entity")
@EnableJpaRepositories("com.pd.archiver.users.repository")
@ComponentScan("com.pd.archiver.users")
public class UserConfig {
}
