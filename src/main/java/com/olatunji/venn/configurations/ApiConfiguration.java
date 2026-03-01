package com.olatunji.venn.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfiguration implements WebMvcConfigurer {

  // Set API versioning strategy to use path segment
  // Endpoint will be accessible as e.g. [/api/{v1}/** OR /api/{v2}/**, etc.]
  // where the path-index of the version number is 1
  @Override
  public void configureApiVersioning(ApiVersionConfigurer configurer) {
    configurer.usePathSegment(1);
  }
}
