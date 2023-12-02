package com.pd.archiver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * The type Archiver application tests.
 */
@SpringBootTest
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
