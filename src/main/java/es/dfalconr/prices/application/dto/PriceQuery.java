package es.dfalconr.prices.application.dto;

import java.time.LocalDateTime;

public record PriceQuery(
    LocalDateTime applicationDate,
    Long productId,
    Long brandId
) {
    public PriceQuery {
        if (applicationDate == null || productId == null || brandId == null) {
            throw new IllegalArgumentException("All query parameters are required");
        }
    }
}
