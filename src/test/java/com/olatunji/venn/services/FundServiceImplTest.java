package com.olatunji.venn.services;

import com.olatunji.venn.configurations.properties.CustomerLimitProperties;
import com.olatunji.venn.domain.entities.Fund;
import com.olatunji.venn.domain.repositories.FundRepository;
import com.olatunji.venn.mappers.FundMapper;
import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class FundServiceImplTest {

    @Mock
    private FundRepository fundRepository;

    @Mock
    private CustomerLimitProperties customerLimitProperties;

    @Mock
    private FundCacheManager fundCacheManager;

    private final FundMapper fundMapper = Mappers.getMapper(FundMapper.class);

    private FundServiceImpl fundService;

    @BeforeEach
    void setUp() {
        fundService = new FundServiceImpl(fundRepository, fundCacheManager, fundMapper, customerLimitProperties);
    }

    @Test
    void given_existingFund_when_loadFundsIntoCustomerAccount_then_returnEmptyFundOutput() {

        FundInput fundInput = new FundInput("1", "1", BigDecimal.TEN, LocalDateTime.now());
        Mockito.when(fundRepository.findByLoadIdAndCustomerId(fundInput.id(), fundInput.customerId()))
                .thenReturn(Optional.of(
                        new Fund(fundInput.id(), fundInput.customerId(), fundInput.loadAmount(), fundInput.time())));

        FundOutput result = fundService.loadFundsIntoCustomerAccount(fundInput);

        Assertions.assertThat(result).isEqualTo(FundOutput.EMPTY_INSTANCE);
        Mockito.verify(fundRepository, Mockito.times(1))
                .findByLoadIdAndCustomerId(fundInput.id(), fundInput.customerId());
        Mockito.verify(fundRepository, Mockito.never()).save(ArgumentMatchers.any(Fund.class));
    }

    @Test
    void given_dailyAmountLimitExceeded_when_loadFundsIntoCustomerAccount_then_returnFundOutputFromInput() {

        FundInput fundInput = new FundInput("1", "1", BigDecimal.TEN, LocalDateTime.now());
        CustomerLimitProperties.CustomerFundLimit dailyAmountLimit =
                new CustomerLimitProperties.CustomerFundLimit(BigDecimal.valueOf(5000), null);
        Mockito.when(customerLimitProperties.getDailyAmountLimit()).thenReturn(dailyAmountLimit);
        Mockito.when(fundCacheManager.getDailyAmountLoaded(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(BigDecimal.valueOf(5000));

        FundOutput result = fundService.loadFundsIntoCustomerAccount(fundInput);

        Assertions.assertThat(result.isAccepted()).isFalse();
        Mockito.verify(fundRepository, Mockito.never()).save(ArgumentMatchers.any(Fund.class));
    }

    @Test
    void given_weeklyAmountLimitExceeded_when_loadFundsIntoCustomerAccount_then_returnFundOutputFromInput() {

        FundInput fundInput = new FundInput("1", "1", BigDecimal.TEN, LocalDateTime.now());
        CustomerLimitProperties.CustomerFundLimit dailyAmountLimit =
                new CustomerLimitProperties.CustomerFundLimit(BigDecimal.valueOf(5000), null);
        CustomerLimitProperties.CustomerFundLimit weeklyAmountLimit =
                new CustomerLimitProperties.CustomerFundLimit(BigDecimal.valueOf(20000), null);
        Mockito.when(customerLimitProperties.getDailyAmountLimit()).thenReturn(dailyAmountLimit);
        Mockito.when(customerLimitProperties.getWeeklyAmountLimit()).thenReturn(weeklyAmountLimit);
        Mockito.when(fundCacheManager.getDailyAmountLoaded(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(BigDecimal.ZERO); // Daily check passes
        Mockito.when(fundCacheManager.getWeeklyAmountLoaded(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(BigDecimal.valueOf(20000)); // Weekly checks fails

        FundOutput result = fundService.loadFundsIntoCustomerAccount(fundInput);

        Assertions.assertThat(result.isAccepted()).isFalse();
        Mockito.verify(fundRepository, Mockito.never()).save(ArgumentMatchers.any(Fund.class));
    }

    @Test
    void given_dailyAttemptsLimitExceeded_when_loadFundsIntoCustomerAccount_then_returnFundOutputFromInput() {

        FundInput fundInput = new FundInput("1", "1", BigDecimal.TEN, LocalDateTime.now());
        Mockito.when(fundRepository.findByLoadIdAndCustomerId(fundInput.id(), fundInput.customerId()))
                .thenReturn(Optional.of(
                        new Fund(fundInput.id(), fundInput.customerId(), fundInput.loadAmount(), fundInput.time())));
        Mockito.when(fundCacheManager.getDailyAmountLoaded(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(BigDecimal.ZERO); // Daily and weekly checks pass
        Mockito.when(fundCacheManager.getDailyLoadAttempts(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(3L); // Daily attempts check fails

        FundOutput result = fundService.loadFundsIntoCustomerAccount(fundInput);

        Assertions.assertThat(result.isAccepted()).isFalse();
        Mockito.verify(fundRepository, Mockito.never()).save(ArgumentMatchers.any(Fund.class));
    }

    @Test
    void given_validFundInput_when_loadFundsIntoCustomerAccount_then_saveAndReturnFundOutput() {

        FundInput fundInput = new FundInput("1", "1", BigDecimal.TEN, LocalDateTime.now());
        CustomerLimitProperties.CustomerFundLimit dailyAmountLimit =
                new CustomerLimitProperties.CustomerFundLimit(BigDecimal.valueOf(2000), null);
        CustomerLimitProperties.CustomerFundLimit weeklyAmountLimit =
                new CustomerLimitProperties.CustomerFundLimit(BigDecimal.valueOf(10000), null);
        // set maxAttempts to a value strictly greater than current attempts to satisfy the check
        CustomerLimitProperties.CustomerFundLimit dailyAttemptsLimit =
                new CustomerLimitProperties.CustomerFundLimit(BigDecimal.ONE, 2);

        Mockito.when(customerLimitProperties.getDailyAmountLimit()).thenReturn(dailyAmountLimit);
        Mockito.when(customerLimitProperties.getWeeklyAmountLimit()).thenReturn(weeklyAmountLimit);
        Mockito.when(customerLimitProperties.getDailyAttemptsLimit()).thenReturn(dailyAttemptsLimit);

        Mockito.when(fundCacheManager.getDailyAmountLoaded(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(BigDecimal.ZERO); // Daily amount check passes: 0 + 10 <= 2000
        Mockito.when(fundCacheManager.getWeeklyAmountLoaded(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(BigDecimal.valueOf(9990)); // Weekly amount check passes: 9990 + 10 = 10000 <= 10000
        Mockito.when(fundCacheManager.getDailyLoadAttempts(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(1L); // Daily attempts check passes: 1 < 2

        FundOutput acceptedOutput = new FundOutput(fundInput.id(), fundInput.customerId(), true);
        Mockito.when(fundCacheManager.createNewFund(ArgumentMatchers.any(FundInput.class)))
                .thenReturn(acceptedOutput);

        FundOutput result = fundService.loadFundsIntoCustomerAccount(fundInput);

        Assertions.assertThat(result.isAccepted()).isTrue();
        Mockito.verify(fundCacheManager, Mockito.times(1)).createNewFund(ArgumentMatchers.any(FundInput.class));
        Mockito.verify(fundRepository, Mockito.never()).save(ArgumentMatchers.any(Fund.class));
    }
}
