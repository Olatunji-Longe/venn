package com.olatunji.venn.services;

import com.olatunji.venn.models.exchange.FundRequest;
import com.olatunji.venn.models.exchange.FundResponse;

public interface FundService {
  FundResponse loadFundsIntoCustomerAccount(FundRequest request);
}
