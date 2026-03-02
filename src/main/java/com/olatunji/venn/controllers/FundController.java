package com.olatunji.venn.controllers;

import com.olatunji.venn.controllers.exchange.FundRequest;
import com.olatunji.venn.controllers.exchange.FundResponse;
import com.olatunji.venn.mappers.FundMapper;
import com.olatunji.venn.services.FundService;
import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/funds")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;
    private final FundMapper mapper;

    @PostMapping(
            path = "/load",
            version = "v1.0",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = """
            Loads funds into customer accounts. Will accept or decline attempts based on velocity limits.
            - Enforces velocity limits per customer:
                - Max $5,000 per day
                - Max $20,000 per week
                - Max 3 loads per day (regardless of amount)
                - Duplicate load IDs per customer are ignored - Will return 204(No Content)
                - Daily boundary is Midnight 00:00:00 UTC; Week start Monday 00:00:00 UTC
        """)
    public ResponseEntity<FundResponse> loadFunds(@Valid @RequestBody FundRequest request) {
        final FundInput fundInput = mapper.toFundInput(request);
        final FundOutput fundOutput = fundService.loadFundsIntoCustomerAccount(fundInput);
        if (fundOutput.isFullyInstantiated()) {
            return ResponseEntity.ok(mapper.toFundResponse(fundOutput));
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
