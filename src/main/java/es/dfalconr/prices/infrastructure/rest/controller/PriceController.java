package es.dfalconr.prices.infrastructure.rest.controller;

import es.dfalconr.prices.application.dto.PriceQuery;
import es.dfalconr.prices.application.dto.PriceResponse;
import es.dfalconr.prices.application.service.GetApplicablePriceService;
import es.dfalconr.prices.infrastructure.rest.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/prices")
@Tag(name = "Prices", description = "Price query operations")
public class PriceController {

    private final GetApplicablePriceService priceService;

    public PriceController(GetApplicablePriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    @Operation(
        summary = "Get applicable price",
        description = "Returns the applicable price for a product at a specific date and brand"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Price found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PriceResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request parameters",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
            examples = @ExampleObject(
                value = """
                {
                  "status": 400,
                  "message": "Required parameter 'productId' is missing",
                  "timestamp": "2020-06-14T10:00:00"
                }
                """
            )
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Price not found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
            examples = @ExampleObject(
                value = """
                {
                  "status": 404,
                  "message": "No price found for product 35455, brand 1 at 2020-06-14T10:00",
                  "timestamp": "2020-06-14T10:00:00"
                }
                """
            )
        )
    )
    public ResponseEntity<PriceResponse> getApplicablePrice(
        @Parameter(description = "Application date (ISO-8601 format)", required = true, example = "2020-06-14T10:00:00")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
        @Parameter(description = "Product identifier", required = true, example = "35455")
        @RequestParam Long productId,
        @Parameter(description = "Brand identifier", required = true, example = "1")
        @RequestParam Long brandId
    ) {
        PriceQuery query = new PriceQuery(applicationDate, productId, brandId);
        PriceResponse response = priceService.execute(query);
        return ResponseEntity.ok(response);
    }
}
