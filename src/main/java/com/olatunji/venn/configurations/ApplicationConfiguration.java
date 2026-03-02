package com.olatunji.venn.configurations;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.olatunji.venn.configurations.properties.CacheConfigProperties;
import com.olatunji.venn.configurations.properties.CustomerLimitProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@EnableCaching
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({CustomerLimitProperties.class, CacheConfigProperties.class})
public class ApplicationConfiguration {

    private final CacheConfigProperties cacheConfigProperties;

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                new CaffeineCache(
                        CacheConfigProperties.DailyAmount.CACHE_NAME,
                        Caffeine.newBuilder()
                                .recordStats()
                                .expireAfterWrite(
                                        cacheConfigProperties.getDailyAmount().getExpireAfterWrite())
                                .maximumSize(
                                        cacheConfigProperties.getDailyAmount().getMaximumSize())
                                .build()),
                new CaffeineCache(
                        CacheConfigProperties.WeeklyAmount.CACHE_NAME,
                        Caffeine.newBuilder()
                                .recordStats()
                                .expireAfterWrite(
                                        cacheConfigProperties.getWeeklyAmount().getExpireAfterWrite())
                                .maximumSize(
                                        cacheConfigProperties.getWeeklyAmount().getMaximumSize())
                                .build()),
                new CaffeineCache(
                        CacheConfigProperties.DailyAttempts.CACHE_NAME,
                        Caffeine.newBuilder()
                                .recordStats()
                                .expireAfterWrite(
                                        cacheConfigProperties.getDailyAttempts().getExpireAfterWrite())
                                .maximumSize(
                                        cacheConfigProperties.getDailyAttempts().getMaximumSize())
                                .build())));

        // Ensure the manager is initialized before wrapping
        cacheManager.afterPropertiesSet();

        // Need cache puts/evicts to happen only after a successful commit:
        return new TransactionAwareCacheManagerProxy(cacheManager);
    }
}
