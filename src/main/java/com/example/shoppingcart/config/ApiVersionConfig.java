package com.example.shoppingcart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    public static final String API_VERSION = "1.0";
    public static final String API_VERSION_HEADER = "X-API-Version";
    public static final String DEPRECATED_VERSION_HEADER = "X-API-Deprecated";
    public static final String SUNSET_HEADER = "Sunset";
    public static final String API_MIN_VERSION = "1.0";
    public static final String API_BASE_PATH = "/api/v1";

    @Bean
    public HandlerInterceptor apiVersionInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
                // Add current API version to all responses
                response.setHeader(API_VERSION_HEADER, API_VERSION);

                // Add standard versioning headers
                response.setHeader("Accept-Version", API_MIN_VERSION + " - " + API_VERSION);

                String requestedVersion = request.getHeader(API_VERSION_HEADER);
                if (requestedVersion != null) {
                    try {
                        double version = Double.parseDouble(requestedVersion);
                        if (version < Double.parseDouble(API_MIN_VERSION)) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            return false;
                        }
                        if (version < Double.parseDouble(API_VERSION)) {
                            response.setHeader(DEPRECATED_VERSION_HEADER, "true");
                            response.setHeader(SUNSET_HEADER, "Sat, 1 Jan 2026 00:00:00 GMT");
                            response.setHeader("Link", "</api/docs>; rel=\"deprecation\"; type=\"text/html\"");
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(apiVersionInterceptor())
               .addPathPatterns(API_BASE_PATH + "/**");
    }
}
