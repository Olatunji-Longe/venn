package com.olatunji.venn.models.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "customer_id", "accepted"})
public class FundResponse {
  @JsonProperty("id")
  private String id;

  @JsonProperty("customer_id")
  private String customerId;

  @JsonProperty("accepted")
  private boolean accepted;
}
