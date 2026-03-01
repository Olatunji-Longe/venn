package com.olatunji.venn.models.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record FundRequest(
    @JsonProperty("id") String id,
    @JsonProperty("customer_id") String customerId,
    @JsonProperty("load_amount") String loadAmount,
    @JsonProperty("time") LocalDateTime time) {}
