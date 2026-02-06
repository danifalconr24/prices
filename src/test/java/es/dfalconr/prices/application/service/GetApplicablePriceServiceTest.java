package es.dfalconr.prices.application.service;

import es.dfalconr.prices.application.dto.PriceQuery;
import es.dfalconr.prices.application.dto.PriceResponse;
import es.dfalconr.prices.domain.exception.PriceNotFoundException;
import es.dfalconr.prices.domain.model.Price;
import es.dfalconr.prices.domain.port.PriceRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicablePriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private GetApplicablePriceService service;

    @Test
    @DisplayName("Should return price when single price found")
    void shouldReturnPriceWhenSinglePriceFound() {
        // Given
        PriceQuery query = new PriceQuery(
            LocalDateTime.of(2020, 6, 14, 10, 0),
            100L,
            1L
        );
        Price price = createPrice(1L, 0, "35.50");
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(price));

        // When
        PriceResponse response = service.execute(query);

        // Then
        assertThat(response.finalPrice())
            .isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(response.priceList()).isEqualTo(1);
        verify(priceRepository).findApplicablePrices(
            query.applicationDate(),
            query.productId(),
            query.brandId()
        );
    }

    @Test
    @DisplayName("Should select highest priority when multiple prices found")
    void shouldSelectHighestPriorityWhenMultiplePricesFound() {
        // Given
        PriceQuery query = new PriceQuery(
            LocalDateTime.of(2020, 6, 14, 16, 0),
            100L,
            1L
        );
        Price lowPriority = createPrice(1L, 0, "35.50");
        Price highPriority = createPrice(2L, 1, "25.45");
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(lowPriority, highPriority));

        // When
        PriceResponse response = service.execute(query);

        // Then
        assertThat(response.priceList()).isEqualTo(2);
        assertThat(response.finalPrice())
            .isEqualByComparingTo(new BigDecimal("25.45"));
    }

    @Test
    @DisplayName("Should select price with priority 1 over priority 0")
    void shouldSelectPriorityOneOverPriorityZero() {
        // Given
        PriceQuery query = new PriceQuery(LocalDateTime.now(), 100L, 1L);
        Price priority0 = createPrice(1L, 0, "35.50");
        Price priority1 = createPrice(2L, 1, "25.45");
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(priority0, priority1));

        // When
        PriceResponse response = service.execute(query);

        // Then
        assertThat(response.priceList()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw exception when no prices found")
    void shouldThrowExceptionWhenNoPricesFound() {
        // Given
        PriceQuery query = new PriceQuery(
            LocalDateTime.of(2020, 6, 14, 10, 0),
            100L,
            1L
        );
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(Collections.emptyList());

        // When / Then
        assertThatThrownBy(() -> service.execute(query))
            .isInstanceOf(PriceNotFoundException.class)
            .hasMessageContaining("No price found")
            .hasMessageContaining("100")
            .hasMessageContaining("1");
    }

    @Test
    @DisplayName("Should handle three prices with different priorities")
    void shouldHandleThreePricesWithDifferentPriorities() {
        // Given
        PriceQuery query = new PriceQuery(LocalDateTime.now(), 100L, 1L);
        Price priority0 = createPrice(1L, 0, "35.50");
        Price priority1 = createPrice(2L, 1, "25.45");
        Price priority2 = createPrice(3L, 2, "30.50");
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(priority0, priority1, priority2));

        // When
        PriceResponse response = service.execute(query);

        // Then
        assertThat(response.priceList()).isEqualTo(3);
        assertThat(response.finalPrice())
            .isEqualByComparingTo(new BigDecimal("30.50"));
    }

    @Test
    @DisplayName("Should handle multiple prices with same priority")
    void shouldHandleMultiplePricesWithSamePriority() {
        // Edge case: if two prices have same priority, max() returns one
        PriceQuery query = new PriceQuery(LocalDateTime.now(), 100L, 1L);
        Price price1 = createPrice(1L, 1, "35.50");
        Price price2 = createPrice(2L, 1, "25.45");
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(price1, price2));

        // When
        PriceResponse response = service.execute(query);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.finalPrice()).isIn(
            new BigDecimal("35.50"),
            new BigDecimal("25.45")
        );
    }

    @Test
    @DisplayName("Should pass correct parameters to repository")
    void shouldPassCorrectParametersToRepository() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 100L;
        Long brandId = 1L;
        PriceQuery query = new PriceQuery(date, productId, brandId);
        Price price = createPrice(1L, 0, "35.50");
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(price));

        // When
        service.execute(query);

        // Then
        verify(priceRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Should map price to response correctly")
    void shouldMapPriceToResponseCorrectly() {
        // Given
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 30, 23, 59);
        PriceQuery query = new PriceQuery(
            LocalDateTime.of(2020, 6, 15, 12, 0),
            100L,
            1L
        );
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
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(price));

        // When
        PriceResponse response = service.execute(query);

        // Then
        assertThat(response.productId()).isEqualTo(100L);
        assertThat(response.brandId()).isEqualTo(1L);
        assertThat(response.priceList()).isEqualTo(1);
        assertThat(response.startDate()).isEqualTo(start);
        assertThat(response.endDate()).isEqualTo(end);
        assertThat(response.finalPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    @DisplayName("Should work with different product and brand IDs")
    void shouldWorkWithDifferentProductAndBrandIds() {
        // Given
        PriceQuery query = new PriceQuery(
            LocalDateTime.now(),
            35455L,
            2L
        );
        Price price = new Price(
            1L,
            2L,
            35455L,
            1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            0,
            new BigDecimal("45.99"),
            "EUR"
        );
        when(priceRepository.findApplicablePrices(any(), any(), any()))
            .thenReturn(List.of(price));

        // When
        PriceResponse response = service.execute(query);

        // Then
        assertThat(response.productId()).isEqualTo(35455L);
        assertThat(response.brandId()).isEqualTo(2L);
        assertThat(response.finalPrice())
            .isEqualByComparingTo(new BigDecimal("45.99"));
    }

    // Helper method to create Price objects for testing
    private Price createPrice(Long priceList, Integer priority, String amount) {
        LocalDateTime now = LocalDateTime.now();
        return new Price(
            1L,
            1L,
            100L,
            priceList.intValue(),
            now,
            now.plusDays(1),
            priority,
            new BigDecimal(amount),
            "EUR"
        );
    }
}
