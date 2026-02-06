package com.onclass.technology.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * R2DBC configuration for enabling auditing features.
 * 
 * Enables automatic population of @CreatedDate and @LastModifiedDate fields
 * in R2DBC entities.
 * 
 * Note: For @CreatedDate and @LastModifiedDate to work properly with R2DBC,
 * the database should handle timestamps (as configured in our schema with
 * DEFAULT CURRENT_TIMESTAMP and ON UPDATE CURRENT_TIMESTAMP).
 */
@Configuration
@EnableR2dbcAuditing
public class R2dbcConfiguration {
    // R2DBC auditing is enabled via annotation
    // Additional R2DBC customizations can be added here if needed
}
