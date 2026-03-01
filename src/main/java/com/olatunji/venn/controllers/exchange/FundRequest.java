package com.olatunji.venn.controllers.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.olatunji.venn.validators.DollarString;
import com.olatunji.venn.validators.IsoDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FundRequest(
        @NotBlank @JsonProperty("id") String id,
        @NotBlank @JsonProperty("customer_id") String customerId,
        @NotNull @DollarString @JsonProperty("load_amount") String loadAmount,
        @NotNull @IsoDateTime @JsonProperty("time") String time) {}
