package es.dfalconr.prices.infrastructure.persistence.adapter;

import es.dfalconr.prices.domain.model.Price;
import es.dfalconr.prices.infrastructure.persistence.entity.PriceJpaEntity;
import es.dfalconr.prices.infrastructure.persistence.repository.PriceJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceRepositoryAdapterTest {

    @Mock
    private PriceJpaRepository jpaRepository;

    @InjectMocks
    private PriceRepositoryAdapter adapter;

    @Test
    @DisplayName("Should call JPA repository with correct parameters")
    void shouldCallJpaRepositoryWithCorrectParameters() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 100L;
        Long brandId = 1L;
        when(jpaRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        adapter.findApplicablePrices(date, productId, brandId);

        // Then
        verify(jpaRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Should map JPA entities to domain models")
    void shouldMapJpaEntitiesToDomainModels() {
        // Given
        PriceJpaEntity entity = createJpaEntity(1L, 100L, 1L, "35.50");
        when(jpaRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(entity));

        // When
        List<Price> result = adapter.findApplicablePrices(
            LocalDateTime.now(), 100L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).productId()).isEqualTo(100L);
        assertThat(result.get(0).brandId()).isEqualTo(1L);
        assertThat(result.get(0).amount()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    @DisplayName("Should return empty list when no entities found")
    void shouldReturnEmptyListWhenNoEntitiesFound() {
        // Given
        when(jpaRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        List<Price> result = adapter.findApplicablePrices(
            LocalDateTime.now(), 100L, 1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should map multiple JPA entities to domain models")
    void shouldMapMultipleJpaEntitiesToDomainModels() {
        // Given
        PriceJpaEntity entity1 = createJpaEntity(1L, 100L, 1L, "35.50");
        PriceJpaEntity entity2 = createJpaEntity(2L, 100L, 1L, "25.45");
        when(jpaRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(entity1, entity2));

        // When
        List<Price> result = adapter.findApplicablePrices(
            LocalDateTime.now(), 100L, 1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(result.get(1).amount()).isEqualByComparingTo(new BigDecimal("25.45"));
    }

    @Test
    @DisplayName("Should preserve all entity fields when mapping to domain")
    void shouldPreserveAllEntityFieldsWhenMapping() {
        // Given
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 30, 23, 59);
        PriceJpaEntity entity = new PriceJpaEntity(
            1L, 2L, 35455L, 1, start, end, 0,
            new BigDecimal("45.99"), "EUR", null, null
        );
        when(jpaRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(entity));

        // When
        List<Price> result = adapter.findApplicablePrices(
            LocalDateTime.now(), 35455L, 2L);

        // Then
        Price price = result.get(0);
        assertThat(price.id()).isEqualTo(1L);
        assertThat(price.brandId()).isEqualTo(2L);
        assertThat(price.productId()).isEqualTo(35455L);
        assertThat(price.priceList()).isEqualTo(1);
        assertThat(price.startDate()).isEqualTo(start);
        assertThat(price.endDate()).isEqualTo(end);
        assertThat(price.priority()).isEqualTo(0);
        assertThat(price.amount()).isEqualByComparingTo(new BigDecimal("45.99"));
        assertThat(price.currency()).isEqualTo("EUR");
    }

    // Helper method to create JPA entities for testing
    private PriceJpaEntity createJpaEntity(Long id, Long productId, Long brandId, String price) {
        LocalDateTime now = LocalDateTime.now();
        PriceJpaEntity entity = new PriceJpaEntity();
        entity.setId(id);
        entity.setBrandId(brandId);
        entity.setProductId(productId);
        entity.setPriceList(1);
        entity.setStartDate(now);
        entity.setEndDate(now.plusDays(1));
        entity.setPriority(0);
        entity.setPrice(new BigDecimal(price));
        entity.setCurrency("EUR");
        return entity;
    }
}
