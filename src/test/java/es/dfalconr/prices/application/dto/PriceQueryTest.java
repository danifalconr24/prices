package es.dfalconr.prices.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceQueryTest {

    @Test
    @DisplayName("Should throw exception when applicationDate is null")
    void shouldThrowExceptionWhenApplicationDateIsNull() {
        assertThatThrownBy(() -> new PriceQuery(null, 100L, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("All query parameters are required");
    }

    @Test
    @DisplayName("Should throw exception when productId is null")
    void shouldThrowExceptionWhenProductIdIsNull() {
        assertThatThrownBy(() -> new PriceQuery(LocalDateTime.now(), null, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("All query parameters are required");
    }

    @Test
    @DisplayName("Should throw exception when brandId is null")
    void shouldThrowExceptionWhenBrandIdIsNull() {
        assertThatThrownBy(() -> new PriceQuery(LocalDateTime.now(), 100L, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("All query parameters are required");
    }

    @Test
    @DisplayName("Should create valid PriceQuery with all parameters")
    void shouldCreateValidPriceQuery() {
        assertThatCode(() -> new PriceQuery(
            LocalDateTime.now(),
            100L,
            1L
        ))
            .doesNotThrowAnyException();
    }
}
