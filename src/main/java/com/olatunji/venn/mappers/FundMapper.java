package com.olatunji.venn.mappers;

import com.olatunji.venn.domain.entities.Fund;
import com.olatunji.venn.models.exchange.FundRequest;
import com.olatunji.venn.models.exchange.FundResponse;
import java.math.BigDecimal;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FundMapper {

  @Mapping(source = "id", target = "loadId")
  @Mapping(
      source = "loadAmount",
      target = "loadAmount",
      qualifiedByName = "amountStringToBigDecimal")
  Fund toNewFund(FundRequest request);

  @Named("amountStringToBigDecimal")
  default BigDecimal amountStringToBigDecimal(String amount) {
    if (amount == null) {
      return null;
    }
    return new BigDecimal(amount.replace("$", ""));
  }

  @Mapping(source = "loadId", target = "id")
  @Mapping(target = "accepted", ignore = true)
  FundResponse toResponse(Fund fund, @Context boolean accepted);

  @AfterMapping
  default void updateAccepted(@MappingTarget FundResponse fundResponse, @Context boolean accepted) {
    fundResponse.setAccepted(accepted);
  }
}
