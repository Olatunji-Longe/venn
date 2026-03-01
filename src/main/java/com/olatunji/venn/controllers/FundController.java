package com.olatunji.venn.controllers;

import com.olatunji.venn.controllers.exchange.FundRequest;
import com.olatunji.venn.controllers.exchange.FundResponse;
import com.olatunji.venn.mappers.FundMapper;
import com.olatunji.venn.services.FundService;
import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;
import jakarta.validation.Valid;
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
    private final FundMapper mapper;

    @PostMapping(path = "/load", version = "1.0")
    public ResponseEntity<FundResponse> loadFunds(@Valid @RequestBody FundRequest request) {
        final FundInput fundInput = mapper.toFundInput(request);
        final FundOutput fundOutput = fundService.loadFundsIntoCustomerAccount(fundInput);
        if (!fundOutput.isEmptyInstance()) {
            return ResponseEntity.ok(mapper.toFundResponse(fundOutput));
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
