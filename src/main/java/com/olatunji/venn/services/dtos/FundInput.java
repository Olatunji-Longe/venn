package com.olatunji.venn.services.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FundInput(String id, String customerId, BigDecimal loadAmount, LocalDateTime time) {}
