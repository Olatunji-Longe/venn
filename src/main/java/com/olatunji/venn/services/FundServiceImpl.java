package com.olatunji.venn.services;

import com.olatunji.venn.mappers.FundMapper;
import com.olatunji.venn.models.exchange.FundRequest;
import com.olatunji.venn.models.exchange.FundResponse;
import com.olatunji.venn.domain.repositories.FundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {

  private final FundRepository fundRepository;
  private final FundMapper mapper;

  public FundResponse loadFundsIntoCustomerAccount(final FundRequest request) {
    return fundRepository
        .findByLoadIdAndCustomerId(request.id(), request.customerId())
        .map(fund -> mapper.toResponse(fund, false))
        .orElseGet(
            () -> {
              var newFund = mapper.toNewFund(request);
              var savedFund = fundRepository.save(newFund);
              return mapper.toResponse(savedFund, true);
            });
  }
}
