package com.example.fulfilment.common;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/*This class is used to set up configuration and infrastructure needed for integration testing.
All integration test suites should extend this class.*/
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(
    TestInstance.Lifecycle.PER_CLASS) // @BeforeAll and @AfterAll methods can be non-static
public abstract class BaseIntegrationSuite {

  private static final String IMAGE = "postgres:12.0";

  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>(IMAGE)
          .withDatabaseName("fulfilment_db")
          .withUsername("testuser")
          .withPassword("testpass");

  @DynamicPropertySource
  static void setDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  /* This code runs once when the BaseIntegrationSuite class is first loaded by JVM. This ensures that
  the container is only started once at the start of the test-run lifecycle and reused throughout
  the whole test-run. It also ensures that the container is started before anything else, which means
  that the container is already running when specific testcases start executing, but also that all the
  dynamic properties (values that only become defined once the container is up) are already available
  when @DynamicPropertySource methods get called.*/
  static {
    POSTGRES
        .start(); /*this will also register a JVM shutdown hook that calls POSTGRES.stop() which ensures container is cleanly stopped when JVM stops*/
  }
}
