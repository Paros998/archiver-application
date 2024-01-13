package com.pd.archiver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

/**
 * The type Archiver application tests.
 */
@SpringBootTest
@ActiveProfiles("devlocal")
class ArchiverApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Context loads.
     */
    @Test
    void contextLoads() {
        Assertions.assertNotNull(applicationContext);
    }

}
