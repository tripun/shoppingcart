package com.example.shoppingcart.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Shopping Cart API",
        version = ApiVersionConfig.API_VERSION,
        description = "API for managing shopping cart and calculating prices with special offers"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Shopping Cart API v1")
                        .version(ApiVersionConfig.API_VERSION)
                        .description("""
                            Shopping cart application API
                            
                            API Version Information:
                            - Current Version: %s
                            - Minimum Supported Version: %s
                            - Base Path: %s
                            
                            Version Headers:
                            - X-API-Version: Specify desired API version
                            - Accept-Version: Shows supported version range
                            - X-API-Deprecated: Indicates if version is deprecated
                            - Sunset: Date when deprecated version will be removed
                            
                            For detailed version information, see /docs/API-VERSIONING.md
                            """.formatted(
                                ApiVersionConfig.API_VERSION,
                                ApiVersionConfig.API_MIN_VERSION,
                                ApiVersionConfig.API_BASE_PATH
                            ))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addParameters("ApiVersion", new Parameter()
                            .in("header")
                            .name(ApiVersionConfig.API_VERSION_HEADER)
                            .description("API Version")
                            .schema(new StringSchema()._default(ApiVersionConfig.API_VERSION)))
                        .addHeaders("X-API-Version", new Header()
                                .description("Current API Version")
                                .schema(new StringSchema()._default(ApiVersionConfig.API_VERSION)))
                        .addHeaders("Accept-Version", new Header()
                                .description("Supported API Version Range")
                                .schema(new StringSchema()._default(ApiVersionConfig.API_MIN_VERSION + " - " + ApiVersionConfig.API_VERSION)))
                        .addHeaders("X-API-Deprecated", new Header()
                                .description("Deprecation Status")
                                .schema(new StringSchema()))
                        .addHeaders(ApiVersionConfig.SUNSET_HEADER, new Header()
                                .description("Date when deprecated version will be removed")
                                .schema(new StringSchema())));
    }
}

