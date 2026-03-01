package com.olatunji.venn.configurations;

import com.olatunji.venn.configurations.properties.CustomerLimitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CustomerLimitProperties.class})
public class ApplicationConfiguration {}
