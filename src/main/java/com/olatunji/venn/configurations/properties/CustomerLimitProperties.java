package com.olatunji.venn.configurations.properties;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "customer-limits")
public class CustomerLimitProperties {

    private final CustomerFundLimit dailyAmountLimit;
    private final CustomerFundLimit weeklyAmountLimit;
    private final CustomerFundLimit dailyAttemptsLimit;

    public record CustomerFundLimit(BigDecimal maxAmount, Integer maxAttempts) {

        // Set defaults via compact constructor
        public CustomerFundLimit {
            if (maxAmount == null) {
                maxAmount = BigDecimal.valueOf(0);
            }
            if (maxAttempts == null) {
                maxAttempts = -1;
            }
        }
    }
}
