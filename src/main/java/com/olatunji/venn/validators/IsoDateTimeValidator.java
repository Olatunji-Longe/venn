package com.olatunji.venn.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class IsoDateTimeValidator implements ConstraintValidator<IsoDateTime, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Handled by @NotNull if needed
        }
        try {
            // ISO_INSTANT will parse any date correctly formatted like the expected input date - e.g
            // 2011-12-03T10:15:30Z
            DateTimeFormatter.ISO_INSTANT.parse(value);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
