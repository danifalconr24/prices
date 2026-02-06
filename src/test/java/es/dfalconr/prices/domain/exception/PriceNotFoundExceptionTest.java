package es.dfalconr.prices.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PriceNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with correct message format")
    void shouldCreateExceptionWithCorrectMessage() {
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 100L;
        Long brandId = 1L;

        PriceNotFoundException exception = new PriceNotFoundException(date, productId, brandId);

        assertThat(exception.getMessage())
            .contains("100")
            .contains("1")
            .contains("2020-06-14");
    }

    @Test
    @DisplayName("Should include all parameters in exception message")
    void shouldIncludeAllParametersInMessage() {
        LocalDateTime date = LocalDateTime.of(2020, 12, 31, 23, 59);
        Long productId = 35455L;
        Long brandId = 2L;

        PriceNotFoundException exception = new PriceNotFoundException(date, productId, brandId);

        assertThat(exception.getMessage())
            .contains("35455")
            .contains("2")
            .contains("No price found");
    }
}
