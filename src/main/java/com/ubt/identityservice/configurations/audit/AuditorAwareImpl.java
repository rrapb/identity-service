package com.ubt.identityservice.configurations.audit;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            Map<String, String> details = (Map<String, String>) authentication.getDetails();

            return Optional.of(details.get("onBehalfOfUser"));
        }
        return Optional.of("System");
    }
}