package com.olatunji.venn.configurations.properties;

import java.time.Duration;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.cache.configs")
public class CacheConfigProperties {

    private DailyAmount dailyAmount;
    private WeeklyAmount weeklyAmount;
    private DailyAttempts dailyAttempts;

    public static class DailyAmount extends CacheConfig {
        public static final String CACHE_NAME = "daily-amount-cache";

        public DailyAmount(long maximumSize, Duration expireAfterWrite) {
            super(maximumSize, expireAfterWrite);
        }
    }

    public static class WeeklyAmount extends CacheConfig {
        public static final String CACHE_NAME = "weekly-amount-cache";

        public WeeklyAmount(long maximumSize, Duration expireAfterWrite) {
            super(maximumSize, expireAfterWrite);
        }
    }

    public static class DailyAttempts extends CacheConfig {
        public static final String CACHE_NAME = "daily-attempts-cache";

        public DailyAttempts(long maximumSize, Duration expireAfterWrite) {
            super(maximumSize, expireAfterWrite);
        }
    }

    @Getter
    @RequiredArgsConstructor
    protected abstract static class CacheConfig {
        protected final long maximumSize;
        protected final Duration expireAfterWrite;
    }
}
