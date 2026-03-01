package com.olatunji.venn.controllers;

import com.olatunji.venn.models.exchange.FundRequest;
import com.olatunji.venn.models.exchange.FundResponse;
import com.olatunji.venn.services.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{version}/funds")
@RequiredArgsConstructor
public class FundController {

  private final FundService fundService;

  @PostMapping(path = "/load", version = "1.0")
  public ResponseEntity<FundResponse> loadFunds(@RequestBody FundRequest request) {
    return ResponseEntity.ok(fundService.loadFundsIntoCustomerAccount(request));
  }
}
