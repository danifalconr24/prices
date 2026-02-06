package es.dfalconr.prices.infrastructure.persistence.repository;

import es.dfalconr.prices.infrastructure.persistence.entity.PriceJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PriceJpaRepositoryIntegrationTest {

    @Autowired
    private PriceJpaRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find prices within date range")
    void shouldFindPricesWithinDateRange() {
        // Given
        PriceJpaEntity entity = createAndPersistEntity(
            LocalDateTime.of(2020, 6, 14, 0, 0),
            LocalDateTime.of(2020, 6, 30, 23, 59),
            100L,
            1L,
            0
        );

        // When
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            LocalDateTime.of(2020, 6, 15, 12, 0), 100L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(entity.getId());
    }

    @Test
    @DisplayName("Should return empty list when date is outside range")
    void shouldReturnEmptyListWhenDateOutsideRange() {
        // Given
        createAndPersistEntity(
            LocalDateTime.of(2020, 6, 14, 0, 0),
            LocalDateTime.of(2020, 6, 30, 23, 59),
            100L,
            1L,
            0
        );

        // When - date after range
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            LocalDateTime.of(2020, 7, 1, 0, 0), 100L, 1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should order results by priority descending")
    void shouldOrderByPriorityDescending() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        createAndPersistEntity(now, now.plusDays(1), 100L, 1L, 0); // Low priority
        createAndPersistEntity(now, now.plusDays(1), 100L, 1L, 1); // High priority
        createAndPersistEntity(now, now.plusDays(1), 100L, 1L, 2); // Highest priority

        // When
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            now.plusHours(1), 100L, 1L);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPriority()).isEqualTo(2);
        assertThat(result.get(1).getPriority()).isEqualTo(1);
        assertThat(result.get(2).getPriority()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should filter by brand ID correctly")
    void shouldFilterByBrandId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        createAndPersistEntity(now, now.plusDays(1), 100L, 1L, 0); // brandId = 1
        createAndPersistEntity(now, now.plusDays(1), 100L, 2L, 0); // brandId = 2

        // When - query for brandId = 1
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            now.plusHours(1), 100L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrandId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should filter by product ID correctly")
    void shouldFilterByProductId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        createAndPersistEntity(now, now.plusDays(1), 100L, 1L, 0); // productId = 100
        createAndPersistEntity(now, now.plusDays(1), 200L, 1L, 0); // productId = 200

        // When - query for productId = 100
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            now.plusHours(1), 100L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should match exact start date (boundary test)")
    void shouldMatchExactStartDate() {
        // Given
        LocalDateTime exactStart = LocalDateTime.of(2020, 6, 14, 0, 0);
        createAndPersistEntity(exactStart, exactStart.plusDays(1), 100L, 1L, 0);

        // When - query at exact start date
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            exactStart, 100L, 1L);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should match exact end date (boundary test)")
    void shouldMatchExactEndDate() {
        // Given
        LocalDateTime exactEnd = LocalDateTime.of(2020, 6, 30, 23, 59);
        createAndPersistEntity(exactEnd.minusDays(1), exactEnd, 100L, 1L, 0);

        // When - query at exact end date
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            exactEnd, 100L, 1L);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should return empty list when no matching brand and product combination")
    void shouldReturnEmptyListWhenNoMatch() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        createAndPersistEntity(now, now.plusDays(1), 100L, 1L, 0);

        // When - query for different brand and product
        List<PriceJpaEntity> result = repository.findApplicablePrices(
            now.plusHours(1), 999L, 999L);

        // Then
        assertThat(result).isEmpty();
    }

    // Helper method to create and persist a price entity
    private PriceJpaEntity createAndPersistEntity(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long productId,
        Long brandId,
        Integer priority
    ) {
        PriceJpaEntity entity = new PriceJpaEntity();
        entity.setBrandId(brandId);
        entity.setProductId(productId);
        entity.setPriceList(1);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setPriority(priority);
        entity.setPrice(new BigDecimal("35.50"));
        entity.setCurrency("EUR");
        entityManager.persist(entity);
        return entity;
    }
}
