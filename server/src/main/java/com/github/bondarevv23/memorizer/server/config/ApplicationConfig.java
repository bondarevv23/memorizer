package com.github.bondarevv23.memorizer.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class ApplicationConfig {
    private AccessType databaseAccessType;
}
