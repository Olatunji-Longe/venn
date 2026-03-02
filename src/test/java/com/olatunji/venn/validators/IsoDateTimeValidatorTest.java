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
class IsoDateTimeValidatorTest {

    private IsoDateTimeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new IsoDateTimeValidator();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(
            strings = {"2011-12-03T10:15:30Z", "2023-01-01T00:00:00Z", "2023-12-31T23:59:59Z", "1970-01-01T00:00:00Z"})
    void given_validIsoDateTime_when_isValid_then_returnTrue(String value) {

        boolean result = validator.isValid(value, context);

        Assertions.assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "2011-12-03 10:15:30",
                "2011/12/03T10:15:30Z",
                "03-12-2011T10:15:30Z",
                "2011-12-03T10:15:30",
                "invalid-date",
                "",
                " "
            })
    void given_invalidIsoDateTime_when_isValid_then_returnFalse(String value) {

        boolean result = validator.isValid(value, context);

        Assertions.assertThat(result).isFalse();
    }
}
