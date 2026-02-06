package es.dfalconr.prices.infrastructure.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PriceControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;

    @Test
    @DisplayName("Test 1: Request at 10:00 on June 14 should return priceList 1")
    void testPriceAt10OnJune14() throws Exception {
        // Given
        String applicationDate = "2020-06-14T10:00:00";

        // When & Then
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", applicationDate)
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
            .andExpect(jsonPath("$.brandId").value(BRAND_ID))
            .andExpect(jsonPath("$.priceList").value(1))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(35.50)));
    }

    @Test
    @DisplayName("Test 2: Request at 16:00 on June 14 should return priceList 2")
    void testPriceAt16OnJune14() throws Exception {
        // Given - At 16:00, both priceList 1 (priority 0) and priceList 2 (priority 1) apply
        // Should select priceList 2 due to higher priority
        String applicationDate = "2020-06-14T16:00:00";

        // When & Then
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", applicationDate)
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
            .andExpect(jsonPath("$.brandId").value(BRAND_ID))
            .andExpect(jsonPath("$.priceList").value(2))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(25.45)));
    }

    @Test
    @DisplayName("Test 3: Request at 21:00 on June 14 should return priceList 1")
    void testPriceAt21OnJune14() throws Exception {
        // Given - At 21:00, priceList 2 has ended (18:30), only priceList 1 applies
        String applicationDate = "2020-06-14T21:00:00";

        // When & Then
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", applicationDate)
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
            .andExpect(jsonPath("$.brandId").value(BRAND_ID))
            .andExpect(jsonPath("$.priceList").value(1))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(35.50)));
    }

    @Test
    @DisplayName("Test 4: Request at 10:00 on June 15 should return priceList 3")
    void testPriceAt10OnJune15() throws Exception {
        // Given - At 10:00 on June 15, both priceList 1 and 3 apply
        // PriceList 3 has priority 1, priceList 1 has priority 0
        String applicationDate = "2020-06-15T10:00:00";

        // When & Then
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", applicationDate)
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
            .andExpect(jsonPath("$.brandId").value(BRAND_ID))
            .andExpect(jsonPath("$.priceList").value(3))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(30.50)));
    }

    @Test
    @DisplayName("Test 5: Request at 21:00 on June 16 should return priceList 4")
    void testPriceAt21OnJune16() throws Exception {
        // Given - At 21:00 on June 16, both priceList 1 and 4 apply
        // Both have different priorities, priceList 4 has priority 1
        String applicationDate = "2020-06-16T21:00:00";

        // When & Then
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", applicationDate)
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
            .andExpect(jsonPath("$.brandId").value(BRAND_ID))
            .andExpect(jsonPath("$.priceList").value(4))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(38.95)));
    }

    @Test
    @DisplayName("Test exception: Price not found should return 404")
    void testPriceNotFound() throws Exception {
        // Given - A date outside all price ranges
        String applicationDate = "2019-01-01T10:00:00";

        // When & Then
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", applicationDate)
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test validation: Missing parameters should return 400")
    void testMissingParameters() throws Exception {
        // When & Then - Missing productId parameter
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isBadRequest());
    }

    // Additional Extended Integration Tests

    @Test
    @DisplayName("Should return 404 for non-existent brand ID")
    void shouldReturn404ForDifferentBrandId() throws Exception {
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", "999")) // Non-existent brand
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 for non-existent product ID")
    void shouldReturn404ForDifferentProductId() throws Exception {
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "99999") // Non-existent product
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for invalid date format")
    void shouldReturn400ForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "invalid-date")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for missing applicationDate")
    void shouldReturn400ForMissingApplicationDate() throws Exception {
        mockMvc.perform(get("/api/prices")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for missing brandId")
    void shouldReturn400ForMissingBrandId() throws Exception {
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", PRODUCT_ID.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle boundary date at exact start of priceList 2")
    void shouldHandleBoundaryDateExactStart() throws Exception {
        // Test exact start date of priceList 2: 2020-06-14 15:00:00
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-14T15:00:00")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.priceList").value(2))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(25.45)));
    }

    @Test
    @DisplayName("Should handle boundary date at exact end of priceList 2")
    void shouldHandleBoundaryDateExactEnd() throws Exception {
        // Test exact end date of priceList 2: 2020-06-14 18:30:00
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-14T18:30:00")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.priceList").value(2))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(25.45)));
    }

    @Test
    @DisplayName("Should return 404 for date before all price ranges")
    void shouldReturn404ForDateBeforeAllPrices() throws Exception {
        // Date before the earliest price startDate
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-13T23:59:59")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 for date after all price ranges")
    void shouldReturn404ForDateAfterAllPrices() throws Exception {
        // Date after the latest price endDate
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2021-01-01T00:00:00")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle exact start date of first price range")
    void shouldHandleExactStartOfFirstPrice() throws Exception {
        // Test exact start date of priceList 1: 2020-06-14 00:00:00
        mockMvc.perform(get("/api/prices")
                .param("applicationDate", "2020-06-14T00:00:00")
                .param("productId", PRODUCT_ID.toString())
                .param("brandId", BRAND_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.priceList").value(1))
            .andExpect(jsonPath("$.finalPrice", comparesEqualTo(35.50)));
    }
}

