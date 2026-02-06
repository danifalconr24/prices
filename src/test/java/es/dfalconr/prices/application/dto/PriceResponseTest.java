package es.dfalconr.prices.application.dto;

import es.dfalconr.prices.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PriceResponseTest {

    @Test
    @DisplayName("Should map all fields from Price to PriceResponse")
    void shouldMapPriceToPriceResponse() {
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
            new BigDecimal("35.50"),
            "EUR"
        );

        PriceResponse response = PriceResponse.from(price);

        assertThat(response.productId()).isEqualTo(100L);
        assertThat(response.brandId()).isEqualTo(1L);
        assertThat(response.priceList()).isEqualTo(1);
        assertThat(response.startDate()).isEqualTo(start);
        assertThat(response.endDate()).isEqualTo(end);
        assertThat(response.finalPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    @DisplayName("Should preserve BigDecimal precision when mapping")
    void shouldPreserveBigDecimalPrecision() {
        LocalDateTime now = LocalDateTime.now();
        Price price = new Price(
            1L,
            1L,
            100L,
            1,
            now,
            now.plusDays(1),
            0,
            new BigDecimal("25.45"),
            "EUR"
        );

        PriceResponse response = PriceResponse.from(price);

        assertThat(response.finalPrice()).isEqualByComparingTo(new BigDecimal("25.45"));
        assertThat(response.finalPrice().scale()).isEqualTo(2);
    }
}
