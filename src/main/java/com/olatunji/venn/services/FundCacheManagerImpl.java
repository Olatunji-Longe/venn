package com.olatunji.venn.services;

import com.olatunji.venn.configurations.properties.CacheConfigProperties;
import com.olatunji.venn.domain.entities.Fund;
import com.olatunji.venn.domain.repositories.FundRepository;
import com.olatunji.venn.mappers.FundMapper;
import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * This class manages caching the normalized daily and weekly aggregates at the service layer
 * and evicts those keys after successful writes. This will drastically reduce the load on the
 * sum(...) and count(...) repository queries while keeping results accurate.
 */
@Slf4j
@Service
@RequiredArgsConstructor
class FundCacheManagerImpl implements FundCacheManager {

    private final FundRepository fundRepository;
    private final FundMapper fundMapper;

    // After saving a new Fund, evict the affected day/week entries to
    // force subsequent reads to recalculate with the new data.
    // Because createNewFund runs inside a transaction, transaction-aware cache updates are
    // being used - (see cache configuration).
    // If multiple writes affecting the same period occur within a single request, this will also avoid cache reads
    // inside the same transaction and only populate the cache on the next read request.
    // Hence, this eviction pattern provides the necessary safeguards to ensure consistency.
    @Caching(
            evict = {
                @CacheEvict(
                        cacheNames = CacheConfigProperties.DailyAmount.CACHE_NAME,
                        key = "#fundInput.customerId() + ':' + #fundInput.time().toLocalDate()"),
                @CacheEvict(
                        cacheNames = CacheConfigProperties.WeeklyAmount.CACHE_NAME,
                        key =
                                "#fundInput.customerId() + ':' + #fundInput.time().toLocalDate().with(T(java.time.temporal.TemporalAdjusters).previousOrSame(T(java.time.DayOfWeek).MONDAY))"),
                @CacheEvict(
                        cacheNames = CacheConfigProperties.DailyAttempts.CACHE_NAME,
                        key = "#fundInput.customerId() + ':' + #fundInput.time().toLocalDate()")
            })
    @Transactional
    @Override
    public FundOutput createNewFund(final FundInput fundInput) {
        Fund newFund = fundMapper.toNewFund(fundInput);
        Fund savedFund = fundRepository.save(newFund);
        return fundMapper.toFundOutput(savedFund);
    }

    @Cacheable(
            cacheNames = CacheConfigProperties.DailyAmount.CACHE_NAME,
            key = "#fundInput.customerId() + ':' + #fundInput.time().toLocalDate()")
    @Override
    public BigDecimal getDailyAmountLoaded(FundInput fundInput) {
        LocalDateTime fundingTime = fundInput.time();
        LocalDateTime start = fundingTime.toLocalDate().atStartOfDay();
        LocalDateTime end = fundingTime.toLocalDate().atTime(LocalTime.MAX);
        return fundRepository.sumLoadAmountByCustomerIdAndDateRange(fundInput.customerId(), start, end);
    }

    @Cacheable(
            cacheNames = CacheConfigProperties.WeeklyAmount.CACHE_NAME,
            key =
                    "#fundInput.customerId() + ':' + #fundInput.time().toLocalDate().with(T(java.time.temporal.TemporalAdjusters).previousOrSame(T(java.time.DayOfWeek).MONDAY))")
    @Override
    public BigDecimal getWeeklyAmountLoaded(FundInput fundInput) {
        LocalDateTime fundingTime = fundInput.time();
        LocalDateTime start = fundingTime
                .toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime end = fundingTime
                .toLocalDate()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .atTime(LocalTime.MAX);
        return fundRepository.sumLoadAmountByCustomerIdAndDateRange(fundInput.customerId(), start, end);
    }

    @Cacheable(
            cacheNames = CacheConfigProperties.DailyAttempts.CACHE_NAME,
            key = "#fundInput.customerId() + ':' + #fundInput.time().toLocalDate()")
    @Override
    public long getDailyLoadAttempts(FundInput fundInput) {
        LocalDateTime fundingTime = fundInput.time();
        LocalDateTime start = fundingTime.toLocalDate().atStartOfDay();
        LocalDateTime end = fundingTime.toLocalDate().atTime(LocalTime.MAX);
        return fundRepository.countByCustomerIdAndDateRange(fundInput.customerId(), start, end);
    }
}
