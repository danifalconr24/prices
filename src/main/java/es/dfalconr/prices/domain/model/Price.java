package es.dfalconr.prices.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Price(
    Long id,
    Long brandId,
    Long productId,
    Integer priceList,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer priority,
    BigDecimal amount,
    String currency
) {
    public Price {
        if (brandId == null || productId == null) {
            throw new IllegalArgumentException("BrandId and ProductId are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (priority == null || priority < 0) {
            throw new IllegalArgumentException("Priority must be non-negative");
        }
    }

    public boolean isApplicableAt(LocalDateTime applicationDate) {
        return !applicationDate.isBefore(startDate) && !applicationDate.isAfter(endDate);
    }
}
