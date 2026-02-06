package es.dfalconr.prices.infrastructure.rest.controller;

import es.dfalconr.prices.application.dto.PriceQuery;
import es.dfalconr.prices.application.dto.PriceResponse;
import es.dfalconr.prices.application.service.GetApplicablePriceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final GetApplicablePriceService priceService;

    public PriceController(GetApplicablePriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public ResponseEntity<PriceResponse> getApplicablePrice(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
        @RequestParam Long productId,
        @RequestParam Long brandId
    ) {
        PriceQuery query = new PriceQuery(applicationDate, productId, brandId);
        PriceResponse response = priceService.execute(query);
        return ResponseEntity.ok(response);
    }
}
