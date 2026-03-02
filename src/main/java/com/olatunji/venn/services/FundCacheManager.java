package com.olatunji.venn.services;

import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;
import java.math.BigDecimal;

interface FundCacheManager {

    FundOutput createNewFund(FundInput fundInput);

    BigDecimal getDailyAmountLoaded(FundInput fundInput);

    BigDecimal getWeeklyAmountLoaded(FundInput fundInput);

    long getDailyLoadAttempts(FundInput fundInput);
}
