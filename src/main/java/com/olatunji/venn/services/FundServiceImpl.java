package com.olatunji.venn.services;

import com.olatunji.venn.configurations.properties.CustomerLimitProperties;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {

    private final FundRepository fundRepository;
    private final FundMapper fundMapper;
    private final CustomerLimitProperties customerLimitProperties;

    @Transactional
    public FundOutput loadFundsIntoCustomerAccount(final FundInput fundInput) {
        return fundRepository
                .findByLoadIdAndCustomerId(fundInput.id(), fundInput.customerId())
                .map(fund -> {
                    log.info(
                            "found existing fund record for loadId: {}, customerId: {} - empty output returned",
                            fundInput.id(),
                            fundInput.customerId());
                    return FundOutput.EMPTY_INSTANCE;
                })
                .orElseGet(() -> processFunding(fundInput));
    }

    private FundOutput processFunding(final FundInput fundInput) {
        return isFundingAccepted(fundInput) ? createNewFund(fundInput) : fundMapper.toFundOutput(fundInput);
    }

    private FundOutput createNewFund(final FundInput fundInput) {
        Fund newFund = fundMapper.toNewFund(fundInput);
        Fund savedFund = fundRepository.save(newFund);
        return fundMapper.toFundOutput(savedFund);
    }

    private boolean isFundingAccepted(final FundInput fundInput) {
        return isDailyFundAmountLimitSatisfied(fundInput)
                && isWeeklyFundAmountLimitSatisfied(fundInput)
                && isDailyFundAttemptsLimitSatisfied(fundInput);
    }

    private boolean isDailyFundAmountLimitSatisfied(final FundInput fundInput) {
        var dailyAmountLimit = customerLimitProperties.getDailyAmountLimit();

        LocalDateTime fundingTime = fundInput.time();
        LocalDateTime startOfDay = fundingTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = fundingTime.toLocalDate().atTime(LocalTime.MAX);

        BigDecimal amountLoadedSoFar =
                fundRepository.sumLoadAmountByCustomerIdAndDateRange(fundInput.customerId(), startOfDay, endOfDay);

        return amountLoadedSoFar.add(fundInput.loadAmount()).compareTo(dailyAmountLimit.maxAmount()) <= 0;
    }

    private boolean isWeeklyFundAmountLimitSatisfied(final FundInput fundInput) {
        var weeklyAmountLimit = customerLimitProperties.getWeeklyAmountLimit();
        LocalDateTime fundingTime = fundInput.time();

        LocalDateTime startOfWeek = fundingTime
                .toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime endOfWeek = fundingTime
                .toLocalDate()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .atTime(LocalTime.MAX);

        BigDecimal amountLoadedSoFar =
                fundRepository.sumLoadAmountByCustomerIdAndDateRange(fundInput.customerId(), startOfWeek, endOfWeek);

        return amountLoadedSoFar.add(fundInput.loadAmount()).compareTo(weeklyAmountLimit.maxAmount()) <= 0;
    }

    private boolean isDailyFundAttemptsLimitSatisfied(final FundInput fundInput) {
        var dailyAttemptsLimit = customerLimitProperties.getDailyAttemptsLimit();

        LocalDateTime fundingTime = fundInput.time();
        LocalDateTime startOfDay = fundingTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = fundingTime.toLocalDate().atTime(LocalTime.MAX);

        long totalAttemptsSoFar =
                fundRepository.countByCustomerIdAndDateRange(fundInput.customerId(), startOfDay, endOfDay);

        return totalAttemptsSoFar < dailyAttemptsLimit.maxAttempts();
    }
}
