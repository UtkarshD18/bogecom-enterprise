package com.bogecom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // Optionally define an AuditorAware bean here if we want to extract the currently authenticated user
}
