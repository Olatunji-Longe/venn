package com.olatunji.venn.domain.repositories;

import com.olatunji.venn.common.RunProfile;
import com.olatunji.venn.domain.entities.Fund;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles({RunProfile.TEST})
class FundRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FundRepository fundRepository;

    @BeforeEach
    void setUp() {
        Fund fund1 = new Fund("load1", "cust1", new BigDecimal("100.00"), LocalDateTime.of(2023, 10, 26, 10, 0));
        Fund fund2 = new Fund("load2", "cust1", new BigDecimal("200.00"), LocalDateTime.of(2023, 10, 26, 12, 0));
        Fund fund3 = new Fund("load3", "cust2", new BigDecimal("300.00"), LocalDateTime.of(2023, 10, 26, 14, 0));

        entityManager.persist(fund1);
        entityManager.persist(fund2);
        entityManager.persist(fund3);
        entityManager.flush();
    }

    @Test
    void given_existingLoadIdAndCustomerId_when_findByLoadIdAndCustomerId_then_returnFund() {

        Optional<Fund> foundFund = fundRepository.findByLoadIdAndCustomerId("load1", "cust1");

        Assertions.assertThat(foundFund).isPresent();
        Assertions.assertThat(foundFund.get().getLoadId()).isEqualTo("load1");
        Assertions.assertThat(foundFund.get().getCustomerId()).isEqualTo("cust1");
    }

    @Test
    void given_nonExistingLoadIdAndCustomerId_when_findByLoadIdAndCustomerId_then_returnEmpty() {

        Optional<Fund> foundFund = fundRepository.findByLoadIdAndCustomerId("nonExistingLoad", "cust1");

        Assertions.assertThat(foundFund).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
        "cust1, 2023-10-26T00:00:00, 2023-10-26T23:59:59, 300.00",
        "cust1, 2023-10-26T09:00:00, 2023-10-26T11:00:00, 100.00",
        "cust2, 2023-10-26T00:00:00, 2023-10-26T23:59:59, 300.00",
        "cust1, 2023-10-27T00:00:00, 2023-10-27T23:59:59, 0"
    })
    void given_customerIdAndDateRange_when_sumLoadAmountByCustomerIdAndDateRange_then_returnCorrectSum(
            String customerId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal expectedSum) {

        BigDecimal sum = fundRepository.sumLoadAmountByCustomerIdAndDateRange(customerId, startTime, endTime);

        Assertions.assertThat(sum).isEqualByComparingTo(expectedSum);
    }

    @ParameterizedTest
    @CsvSource({
        "cust1, 2023-10-26T00:00:00, 2023-10-26T23:59:59, 2",
        "cust1, 2023-10-26T09:00:00, 2023-10-26T11:00:00, 1",
        "cust2, 2023-10-26T00:00:00, 2023-10-26T23:59:59, 1",
        "cust1, 2023-10-27T00:00:00, 2023-10-27T23:59:59, 0"
    })
    void given_customerIdAndDateRange_when_countByCustomerIdAndDateRange_then_returnCorrectCount(
            String customerId, LocalDateTime startTime, LocalDateTime endTime, long expectedCount) {

        long count = fundRepository.countByCustomerIdAndDateRange(customerId, startTime, endTime);

        Assertions.assertThat(count).isEqualTo(expectedCount);
    }
}
