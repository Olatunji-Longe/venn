package com.olatunji.venn.services;

import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;

public interface FundService {
    FundOutput loadFundsIntoCustomerAccount(FundInput fundInput);
}
