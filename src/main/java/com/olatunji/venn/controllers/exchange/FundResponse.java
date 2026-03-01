package com.olatunji.venn.controllers.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "customer_id", "accepted"})
public record FundResponse(
        @JsonProperty("id") String id,
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("accepted") boolean accepted) {}
