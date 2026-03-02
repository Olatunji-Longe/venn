package com.olatunji.venn.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DollarStringValidatorTest {

    private DollarStringValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DollarStringValidator();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"$123.45", "$100.00", "$123456789.99"})
    void given_validDollarString_when_isValid_then_returnTrue(String value) {

        boolean result = validator.isValid(value, context);

        Assertions.assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123.45", "$123.4", "$123.456", "$12a.45", "", " ", "$", "random"})
    void given_invalidDollarString_when_isValid_then_returnFalse(String value) {

        boolean result = validator.isValid(value, context);

        Assertions.assertThat(result).isFalse();
    }
}
