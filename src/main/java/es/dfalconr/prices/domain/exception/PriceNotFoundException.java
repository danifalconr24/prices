package es.dfalconr.prices.domain.exception;

import java.time.LocalDateTime;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(LocalDateTime date, Long productId, Long brandId) {
        super(String.format(
            "No price found for product %d, brand %d at %s",
            productId, brandId, date
        ));
    }
}
