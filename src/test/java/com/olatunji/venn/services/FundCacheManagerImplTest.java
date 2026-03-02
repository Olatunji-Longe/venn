package com.olatunji.venn.services;

import com.olatunji.venn.common.RunProfile;
import com.olatunji.venn.domain.repositories.FundRepository;
import com.olatunji.venn.services.dtos.FundInput;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@ActiveProfiles({RunProfile.TEST})
class FundCacheManagerImplTest {

    private static final String CUSTOMER_ID = "test-customer-id";
    private static final LocalDateTime FUNDING_TIME = LocalDateTime.now();
    private static final FundInput FUND_INPUT =
            new FundInput(UUID.randomUUID().toString(), CUSTOMER_ID, BigDecimal.TEN, FUNDING_TIME);

    @Autowired
    private FundCacheManager fundCacheManager;

    @MockitoSpyBean
    private FundRepository fundRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @Test
    void given_multipleCallsWithSameDay_when_getDailyAmountLoaded_then_cacheResultAndReturnCachedValue() {

        fundCacheManager.getDailyAmountLoaded(FUND_INPUT);
        fundCacheManager.getDailyAmountLoaded(FUND_INPUT);
        fundCacheManager.getDailyAmountLoaded(FUND_INPUT);

        Mockito.verify(fundRepository, Mockito.times(1))
                .sumLoadAmountByCustomerIdAndDateRange(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class));
    }

    @Test
    void given_multipleCallsWithSameWeek_when_getWeeklyAmountLoaded_then_cacheResultAndReturnCachedValue() {

        fundCacheManager.getWeeklyAmountLoaded(FUND_INPUT);
        fundCacheManager.getWeeklyAmountLoaded(FUND_INPUT);
        fundCacheManager.getWeeklyAmountLoaded(FUND_INPUT);

        Mockito.verify(fundRepository, Mockito.times(1))
                .sumLoadAmountByCustomerIdAndDateRange(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class));
    }

    @Test
    void given_multipleCallsWithSameDay_when_getDailyLoadAttempts_then_cacheResultAndReturnCachedValue() {

        fundCacheManager.getDailyLoadAttempts(FUND_INPUT);
        fundCacheManager.getDailyLoadAttempts(FUND_INPUT);
        fundCacheManager.getDailyLoadAttempts(FUND_INPUT);

        Mockito.verify(fundRepository, Mockito.times(1))
                .countByCustomerIdAndDateRange(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class));
    }

    @ParameterizedTest
    @MethodSource("provideCacheKeys")
    void given_cachedValues_when_createNewFund_then_evictCaches(
            String dailyAmountCacheKey, String weeklyAmountCacheKey, String dailyAttemptsCacheKey) {

        fundCacheManager.getDailyAmountLoaded(FUND_INPUT);
        fundCacheManager.getWeeklyAmountLoaded(FUND_INPUT);
        fundCacheManager.getDailyLoadAttempts(FUND_INPUT);

        Assertions.assertThat(cacheManager.getCache("daily-amount-cache").get(dailyAmountCacheKey))
                .isNotNull();
        Assertions.assertThat(cacheManager.getCache("weekly-amount-cache").get(weeklyAmountCacheKey))
                .isNotNull();
        Assertions.assertThat(cacheManager.getCache("daily-attempts-cache").get(dailyAttemptsCacheKey))
                .isNotNull();

        fundCacheManager.createNewFund(FUND_INPUT);

        Assertions.assertThat(cacheManager.getCache("daily-amount-cache").get(dailyAmountCacheKey))
                .isNull();
        Assertions.assertThat(cacheManager.getCache("weekly-amount-cache").get(weeklyAmountCacheKey))
                .isNull();
        Assertions.assertThat(cacheManager.getCache("daily-attempts-cache").get(dailyAttemptsCacheKey))
                .isNull();
    }

    private static Stream<Arguments> provideCacheKeys() {
        String dailyAmountCacheKey = CUSTOMER_ID + ":" + FUNDING_TIME.toLocalDate();
        String weeklyAmountCacheKey =
                CUSTOMER_ID + ":" + FUNDING_TIME.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String dailyAttemptsCacheKey = CUSTOMER_ID + ":" + FUNDING_TIME.toLocalDate();

        return Stream.of(Arguments.of(dailyAmountCacheKey, weeklyAmountCacheKey, dailyAttemptsCacheKey));
    }
}
