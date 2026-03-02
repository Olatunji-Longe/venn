package com.olatunji.venn.services;

import com.olatunji.venn.configurations.properties.CustomerLimitProperties;
import com.olatunji.venn.domain.repositories.FundRepository;
import com.olatunji.venn.mappers.FundMapper;
import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {

    private final FundRepository fundRepository;
    private final FundCacheManager fundCacheManager;
    private final FundMapper fundMapper;
    private final CustomerLimitProperties customerLimitProperties;

    @Transactional
    public FundOutput loadFundsIntoCustomerAccount(final FundInput fundInput) {
        return fundRepository
                .findByLoadIdAndCustomerId(fundInput.id(), fundInput.customerId())
                .map(fund -> FundOutput.EMPTY_INSTANCE)
                .orElseGet(() -> {
                    if (isFundingAccepted(fundInput)) {
                        return fundCacheManager.createNewFund(fundInput);
                    } else {
                        return fundMapper.toFundOutput(fundInput);
                    }
                });
    }

    private boolean isFundingAccepted(final FundInput fundInput) {
        return isDailyFundAmountLimitSatisfied(fundInput)
                && isWeeklyFundAmountLimitSatisfied(fundInput)
                && isDailyFundAttemptsLimitSatisfied(fundInput);
    }

    private boolean isDailyFundAmountLimitSatisfied(final FundInput fundInput) {
        var dailyAmountLimit = customerLimitProperties.getDailyAmountLimit();
        return fundCacheManager
                        .getDailyAmountLoaded(fundInput)
                        .add(fundInput.loadAmount())
                        .compareTo(dailyAmountLimit.maxAmount())
                <= 0;
    }

    private boolean isWeeklyFundAmountLimitSatisfied(final FundInput fundInput) {
        var weeklyAmountLimit = customerLimitProperties.getWeeklyAmountLimit();
        return fundCacheManager
                        .getWeeklyAmountLoaded(fundInput)
                        .add(fundInput.loadAmount())
                        .compareTo(weeklyAmountLimit.maxAmount())
                <= 0;
    }

    private boolean isDailyFundAttemptsLimitSatisfied(final FundInput fundInput) {
        var dailyAttemptsLimit = customerLimitProperties.getDailyAttemptsLimit();
        return fundCacheManager.getDailyLoadAttempts(fundInput) < dailyAttemptsLimit.maxAttempts();
    }
}
