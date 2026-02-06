package es.dfalconr.prices.infrastructure.rest.exception;

import es.dfalconr.prices.domain.exception.PriceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 404 for PriceNotFoundException")
    void shouldReturn404ForPriceNotFoundException() {
        // Given
        PriceNotFoundException exception = new PriceNotFoundException(
            LocalDateTime.now(), 100L, 1L);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handlePriceNotFound(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should return 400 for IllegalArgumentException")
    void shouldReturn400ForIllegalArgumentException() {
        // Given
        IllegalArgumentException exception =
            new IllegalArgumentException("Invalid parameter");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleIllegalArgument(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should include exception message in error response")
    void shouldIncludeMessageInErrorResponse() {
        // Given
        String errorMessage = "Test error message";
        IllegalArgumentException exception =
            new IllegalArgumentException(errorMessage);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleIllegalArgument(exception);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void shouldIncludeTimestampInErrorResponse() {
        // Given
        LocalDateTime before = LocalDateTime.now();
        PriceNotFoundException exception = new PriceNotFoundException(
            LocalDateTime.now(), 100L, 1L);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handlePriceNotFound(exception);
        LocalDateTime after = LocalDateTime.now();

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp())
            .isAfterOrEqualTo(before)
            .isBeforeOrEqualTo(after);
    }

    @Test
    @DisplayName("Should include all error details for PriceNotFoundException")
    void shouldIncludeAllErrorDetailsForPriceNotFoundException() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 100L;
        Long brandId = 1L;
        PriceNotFoundException exception = new PriceNotFoundException(date, productId, brandId);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handlePriceNotFound(exception);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
            .contains("100")
            .contains("1")
            .contains("No price found");
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().timestamp()).isNotNull();
    }
}
