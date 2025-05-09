package com.example.fulfilment.common;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

//This class is used to set up configuration and infrastructure needed for integration testing.
//All integration test suites should extend this class.
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public abstract class BaseIntegrationSuite {

    private static final String IMAGE = "postgres:12.0";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(IMAGE)
                    .withDatabaseName("fulfilment_db")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
