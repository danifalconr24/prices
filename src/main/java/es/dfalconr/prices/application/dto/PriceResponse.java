package es.dfalconr.prices.application.dto;

import es.dfalconr.prices.domain.model.Price;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Price information response")
public record PriceResponse(
    @Schema(description = "Product ID", example = "35455")
    Long productId,

    @Schema(description = "Brand ID", example = "1")
    Long brandId,

    @Schema(description = "Price list identifier", example = "1")
    Integer priceList,

    @Schema(description = "Price validity start date", example = "2020-06-14T00:00:00")
    LocalDateTime startDate,

    @Schema(description = "Price validity end date", example = "2020-12-31T23:59:59")
    LocalDateTime endDate,

    @Schema(description = "Final price amount", example = "35.50")
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
