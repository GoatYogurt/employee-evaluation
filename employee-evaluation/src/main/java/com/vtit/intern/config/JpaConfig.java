package com.vtit.intern.config;

import com.vtit.intern.models.Employee;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.text.html.Option;
import java.util.Objects;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("anonymous");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof Employee) {
                return Optional.of(((Employee) principal).getUsername());
            }

            return Optional.of(authentication.getName());
        };
    }
}
