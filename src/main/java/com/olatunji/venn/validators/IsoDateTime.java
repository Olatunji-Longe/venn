package com.olatunji.venn.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoDateTimeValidator.class)
@Documented
public @interface IsoDateTime {
    String message() default "Must be a valid ISO 8601 date-time formatted as (YYYY-MM-DD'T'HH:MM:SS'Z')";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
