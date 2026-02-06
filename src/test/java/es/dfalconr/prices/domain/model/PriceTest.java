package es.dfalconr.prices.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    @DisplayName("Should throw exception when brandId is null")
    void shouldThrowExceptionWhenBrandIdIsNull() {
        assertThatThrownBy(() -> new Price(
            1L,
            null, // brandId null
            100L,
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            0,
            BigDecimal.TEN,
            "EUR"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("BrandId and ProductId are required");
    }

    @Test
    @DisplayName("Should throw exception when productId is null")
    void shouldThrowExceptionWhenProductIdIsNull() {
        assertThatThrownBy(() -> new Price(
            1L,
            1L,
            null, // productId null
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            0,
            BigDecimal.TEN,
            "EUR"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("BrandId and ProductId are required");
    }

    @Test
    @DisplayName("Should throw exception when startDate is after endDate")
    void shouldThrowExceptionWhenStartDateAfterEndDate() {
        LocalDateTime now = LocalDateTime.now();
        assertThatThrownBy(() -> new Price(
            1L,
            1L,
            100L,
            1,
            now.plusDays(1), // startDate after endDate
            now,
            0,
            BigDecimal.TEN,
            "EUR"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Start date must be before end date");
    }

    @Test
    @DisplayName("Should throw exception when priority is null")
    void shouldThrowExceptionWhenPriorityIsNull() {
        assertThatThrownBy(() -> new Price(
            1L,
            1L,
            100L,
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            null, // priority null
            BigDecimal.TEN,
            "EUR"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Priority must be non-negative");
    }

    @Test
    @DisplayName("Should throw exception when priority is negative")
    void shouldThrowExceptionWhenPriorityIsNegative() {
        assertThatThrownBy(() -> new Price(
            1L,
            1L,
            100L,
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            -1, // negative priority
            BigDecimal.TEN,
            "EUR"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Priority must be non-negative");
    }

    @Test
    @DisplayName("Should allow priority zero (boundary test)")
    void shouldAllowPriorityZero() {
        assertThatCode(() -> new Price(
            1L,
            1L,
            100L,
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            0, // priority zero is valid
            BigDecimal.TEN,
            "EUR"
        ))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should allow startDate equals endDate (boundary test)")
    void shouldAllowStartDateEqualsEndDate() {
        LocalDateTime sameDate = LocalDateTime.now();
        assertThatCode(() -> new Price(
            1L,
            1L,
            100L,
            1,
            sameDate,
            sameDate, // same date is valid
            0,
            BigDecimal.TEN,
            "EUR"
        ))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should return true when date is within range")
    void shouldReturnTrueWhenDateIsWithinRange() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 30, 23, 59);
        Price price = new Price(
            1L,
            1L,
            100L,
            1,
            start,
            end,
            0,
            BigDecimal.TEN,
            "EUR"
        );

        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 12, 0);

        assertThat(price.isApplicableAt(applicationDate)).isTrue();
    }

    @Test
    @DisplayName("Should return false when date is before range")
    void shouldReturnFalseWhenDateIsBeforeRange() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 30, 23, 59);
        Price price = new Price(
            1L,
            1L,
            100L,
            1,
            start,
            end,
            0,
            BigDecimal.TEN,
            "EUR"
        );

        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 13, 23, 59);

        assertThat(price.isApplicableAt(applicationDate)).isFalse();
    }

    @Test
    @DisplayName("Should return false when date is after range")
    void shouldReturnFalseWhenDateIsAfterRange() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 30, 23, 59);
        Price price = new Price(
            1L,
            1L,
            100L,
            1,
            start,
            end,
            0,
            BigDecimal.TEN,
            "EUR"
        );

        LocalDateTime applicationDate = LocalDateTime.of(2020, 7, 1, 0, 0);

        assertThat(price.isApplicableAt(applicationDate)).isFalse();
    }

    @Test
    @DisplayName("Should return true when date equals startDate (boundary test)")
    void shouldReturnTrueWhenDateEqualsStartDate() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        Price price = new Price(
            1L,
            1L,
            100L,
            1,
            start,
            start.plusDays(1),
            0,
            BigDecimal.TEN,
            "EUR"
        );

        assertThat(price.isApplicableAt(start)).isTrue();
    }

    @Test
    @DisplayName("Should return true when date equals endDate (boundary test)")
    void shouldReturnTrueWhenDateEqualsEndDate() {
        LocalDateTime end = LocalDateTime.of(2020, 6, 30, 23, 59);
        Price price = new Price(
            1L,
            1L,
            100L,
            1,
            end.minusDays(1),
            end,
            0,
            BigDecimal.TEN,
            "EUR"
        );

        assertThat(price.isApplicableAt(end)).isTrue();
    }
}
