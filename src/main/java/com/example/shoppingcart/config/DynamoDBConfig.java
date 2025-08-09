package com.example.shoppingcart.config;

import org.springframework.context.annotation.Configuration;

/**
 * This configuration class has been intentionally disabled.
 * The previous logic for running a local, embedded DynamoDB server programmatically
 * has been deprecated in favor of using Testcontainers for integration tests or a
 * standalone Docker container for local development.
 * <p>
 * The connection to the local DynamoDB instance is now configured in {@link AppConfig}.
 * <p>
 * This class was using AWS SDK v1 packages (com.amazonaws.*) which conflict with the
 * project's AWS SDK v2 standard (software.amazon.awssdk.*), causing compilation errors.
 */
@Configuration
public class DynamoDBConfig {
    // All beans and logic have been removed to prevent dependency conflicts and runtime errors.
}
