package com.olatunji.venn.configurations;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ApiVersionParser;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfiguration implements WebMvcConfigurer {

    // Set API versioning strategy to use for API versioning
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {

        // Endpoint will be accessible as e.g. [/api/{v1}/** OR /api/{v2}/**, etc.]
        // where the path-index of the version number is 1
        configurer.usePathSegment(1);

        // Tells Spring NOT to throw a 400 if the version segment is missing e.g., for swagger docs
        configurer.setVersionRequired(false);

        // Sets a default version for unversioned paths
        configurer.setDefaultVersion("v1.0");

        // Custom parser to ensure Swagger UI assets aren't treated as API versions
        configurer.setVersionParser(new SwaggerAwareApiVersionParser());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Prefix all RestControllers with /api/v{version},
        // but EXCLUDE springdoc internal controllers to keep docs accessible
        configurer.addPathPrefix(
                "/api/{version}",
                HandlerTypePredicate.forAnnotation(RestController.class)
                        .and(HandlerTypePredicate.forBasePackage("org.springdoc")
                                .negate()));
    }

    /**
     * Custom parser to ensure Swagger UI assets aren't erroneously treated as API versions.
     */
    static class SwaggerAwareApiVersionParser implements ApiVersionParser {
        @Override
        public Comparable parseVersion(String version) {
            // List of static assets to ignore for versioning
            List<String> swaggerAssets = List.of(
                    "api-docs",
                    "swagger-ui.html",
                    "swagger-ui-bundle.js",
                    "swagger-ui.css",
                    "favicon-32x32.png",
                    "index.html");

            if (swaggerAssets.contains(version)) {
                return null; // Signals this is NOT a versioned endpoint
            }
            return version;
        }
    }
}
