package es.dfalconr.prices.domain.port;

import es.dfalconr.prices.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository {
    List<Price> findApplicablePrices(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
    );
}
