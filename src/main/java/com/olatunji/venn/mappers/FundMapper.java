package com.olatunji.venn.mappers;

import com.olatunji.venn.controllers.exchange.FundRequest;
import com.olatunji.venn.controllers.exchange.FundResponse;
import com.olatunji.venn.domain.entities.Fund;
import com.olatunji.venn.services.dtos.FundInput;
import com.olatunji.venn.services.dtos.FundOutput;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FundMapper {

    @Mapping(source = "loadAmount", target = "loadAmount", qualifiedByName = "amountStringToBigDecimal")
    @Mapping(source = "time", target = "time", qualifiedByName = "isoTimeStringToLocalDateTime")
    FundInput toFundInput(FundRequest request);

    @Named("amountStringToBigDecimal")
    default BigDecimal amountStringToBigDecimal(String amount) {
        if (amount == null) {
            return null;
        }
        return new BigDecimal(amount.replace("$", ""));
    }

    @Named("isoTimeStringToLocalDateTime")
    default LocalDateTime isoTimeStringToLocalDateTime(String isoTimeString) {
        if (isoTimeString == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.parse(isoTimeString), ZoneOffset.UTC);
    }

    // Whenever there's a need to map from FundInput directly to Fund,
    // it means we're creating a new fund that'll need to get persisted to the database
    @Mapping(source = "id", target = "loadId")
    Fund toNewFund(FundInput input);

    // Whenever there's a need to map from FundInput directly to FundOutput,
    // it means the input was NOT accepted - hence accepted is constantly false
    @Mapping(target = "accepted", constant = "false")
    FundOutput toFundOutput(FundInput input);

    // Whenever there's a need to map from Fund entity to FundOutput,
    // it means the input was accepted - hence accepted is constantly true
    @Mapping(source = "loadId", target = "id")
    @Mapping(target = "accepted", constant = "true")
    FundOutput toFundOutput(Fund fund);

    FundResponse toFundResponse(FundOutput output);
}
