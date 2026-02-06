package es.dfalconr.prices.infrastructure.persistence.entity;

import es.dfalconr.prices.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PriceJpaEntityTest {

    @Test
    @DisplayName("Should map JPA entity to domain model correctly")
    void shouldMapToDomainCorrectly() {
        // Given
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 30, 23, 59);

        PriceJpaEntity entity = new PriceJpaEntity();
        entity.setId(1L);
        entity.setBrandId(1L);
        entity.setProductId(100L);
        entity.setPriceList(1);
        entity.setStartDate(start);
        entity.setEndDate(end);
        entity.setPriority(0);
        entity.setPrice(new BigDecimal("35.50"));
        entity.setCurrency("EUR");

        // When
        Price domain = entity.toDomain();

        // Then
        assertThat(domain.id()).isEqualTo(1L);
        assertThat(domain.brandId()).isEqualTo(1L);
        assertThat(domain.productId()).isEqualTo(100L);
        assertThat(domain.priceList()).isEqualTo(1);
        assertThat(domain.startDate()).isEqualTo(start);
        assertThat(domain.endDate()).isEqualTo(end);
        assertThat(domain.priority()).isEqualTo(0);
        assertThat(domain.amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(domain.currency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should handle null optional fields when mapping to domain")
    void shouldHandleNullOptionalFields() {
        // Given - lastUpdate and lastUpdateBy are optional
        PriceJpaEntity entity = createMinimalEntity();
        entity.setLastUpdate(null);
        entity.setLastUpdateBy(null);

        // When/Then - should not throw exception
        assertThatCode(() -> entity.toDomain()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should preserve BigDecimal precision when mapping")
    void shouldPreserveBigDecimalPrecision() {
        // Given
        PriceJpaEntity entity = createMinimalEntity();
        entity.setPrice(new BigDecimal("25.45"));

        // When
        Price domain = entity.toDomain();

        // Then
        assertThat(domain.amount()).isEqualByComparingTo(new BigDecimal("25.45"));
        assertThat(domain.amount().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should map different product and brand IDs correctly")
    void shouldMapDifferentProductAndBrandIds() {
        // Given
        PriceJpaEntity entity = createMinimalEntity();
        entity.setBrandId(2L);
        entity.setProductId(35455L);

        // When
        Price domain = entity.toDomain();

        // Then
        assertThat(domain.brandId()).isEqualTo(2L);
        assertThat(domain.productId()).isEqualTo(35455L);
    }

    // Helper method to create a minimal valid entity
    private PriceJpaEntity createMinimalEntity() {
        PriceJpaEntity entity = new PriceJpaEntity();
        entity.setId(1L);
        entity.setBrandId(1L);
        entity.setProductId(100L);
        entity.setPriceList(1);
        entity.setStartDate(LocalDateTime.now());
        entity.setEndDate(LocalDateTime.now().plusDays(1));
        entity.setPriority(0);
        entity.setPrice(BigDecimal.TEN);
        entity.setCurrency("EUR");
        return entity;
    }
}
