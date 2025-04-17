package com.pikecape.springboot.testing.configuration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresContainerInitializer implements
    ApplicationContextInitializer<ConfigurableApplicationContext>, AfterAllCallback {

  private static final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer(
      DockerImageName.parse("postgres:latest")
  )
      .withDatabaseName("postgres")
      .withUsername("postgres")
      .withPassword("postgres");

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    postgreSqlContainer.start();

    TestPropertyValues.of(
        "spring.datasource.url=" + postgreSqlContainer.getJdbcUrl(),
        "spring.datasource.username=" + postgreSqlContainer.getUsername(),
        "spring.datasource.password=" + postgreSqlContainer.getPassword()
    )
        .applyTo(applicationContext.getEnvironment());
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    if (postgreSqlContainer == null) {
      return;
    }

    postgreSqlContainer.close();
  }
}
