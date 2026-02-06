package es.dfalconr.prices.application.dto;

import es.dfalconr.prices.domain.model.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceResponse(
    Long productId,
    Long brandId,
    Integer priceList,
    LocalDateTime startDate,
    LocalDateTime endDate,
    BigDecimal finalPrice
) {
    public static PriceResponse from(Price price) {
        return new PriceResponse(
            price.productId(),
            price.brandId(),
            price.priceList(),
            price.startDate(),
            price.endDate(),
            price.amount()
        );
    }
}
