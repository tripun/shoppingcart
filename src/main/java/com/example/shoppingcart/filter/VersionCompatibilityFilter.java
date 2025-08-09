package com.example.shoppingcart.filter;

import com.example.shoppingcart.config.ApiVersionConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Order(1)
public class VersionCompatibilityFilter implements Filter {

    private static final String MIN_SUPPORTED_VERSION = "1.0";
    private static final LocalDate SUNSET_DATE = LocalDate.of(2026, 1, 1);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestedVersion = httpRequest.getHeader(ApiVersionConfig.API_VERSION_HEADER);

        // Always add current version to response
        httpResponse.setHeader(ApiVersionConfig.API_VERSION_HEADER, ApiVersionConfig.API_VERSION);

        // Skip version check for non-API paths
        if (!httpRequest.getRequestURI().startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // Check for deprecated or unsupported versions
        if (requestedVersion != null && !requestedVersion.equals(ApiVersionConfig.API_VERSION)) {
            if (requestedVersion.compareTo(MIN_SUPPORTED_VERSION) < 0) {
                httpResponse.setStatus(HttpStatus.GONE.value());
                httpResponse.getWriter().write("This API version is no longer supported");
                return;
            }

            // Mark as deprecated
            httpResponse.setHeader(ApiVersionConfig.DEPRECATED_VERSION_HEADER, "true");
            httpResponse.setHeader(ApiVersionConfig.SUNSET_HEADER,
                SUNSET_DATE.format(DateTimeFormatter.ISO_DATE));
        }

        chain.doFilter(request, response);
    }
}
