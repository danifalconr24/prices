package es.dfalconr.prices.application.service;

import es.dfalconr.prices.application.dto.PriceQuery;
import es.dfalconr.prices.application.dto.PriceResponse;
import es.dfalconr.prices.domain.exception.PriceNotFoundException;
import es.dfalconr.prices.domain.model.Price;
import es.dfalconr.prices.domain.port.PriceRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GetApplicablePriceService {

    private final PriceRepository priceRepository;

    public GetApplicablePriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public PriceResponse execute(PriceQuery query) {
        List<Price> applicablePrices = priceRepository.findApplicablePrices(
            query.applicationDate(),
            query.productId(),
            query.brandId()
        );

        // Business rule: Select price with highest priority
        Price selectedPrice = applicablePrices.stream()
            .max(Comparator.comparing(Price::priority))
            .orElseThrow(() -> new PriceNotFoundException(
                query.applicationDate(),
                query.productId(),
                query.brandId()
            ));

        return PriceResponse.from(selectedPrice);
    }
}
