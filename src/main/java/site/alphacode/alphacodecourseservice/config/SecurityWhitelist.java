package site.alphacode.alphacodecourseservice.config;

import org.springframework.stereotype.Component;

@Component
public class SecurityWhitelist {

    // Permit all methods
    public static final String[] GENERAL_WHITELIST = {
            "/v1/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger",
            "/docs",
            "/",
            "/api/v1/auth/**",
    };

    // Permit GET only
    public static final String[] GET_WHITELIST = {
            "/api/v1/**"
    };
}
