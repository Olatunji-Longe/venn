package com.olatunji.venn.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.math.NumberUtils;

public class DollarStringValidator implements ConstraintValidator<DollarString, String> {

    private static final String CURRENCY_PATTERN = "^\\$\\d+\\.\\d{2}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Handled by @NotNull if needed
        }
        if (!value.matches(CURRENCY_PATTERN)) {
            return false;
        }
        return NumberUtils.isParsable(value.replace("$", ""));
    }
}
