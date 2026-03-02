package com.olatunji.venn.services.dtos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FundOutput {
    private String id;
    private String customerId;
    private boolean accepted;

    public static final FundOutput EMPTY_INSTANCE = new FundOutput();

    public boolean isFullyInstantiated() {
        return null != id && null != customerId;
    }
}
