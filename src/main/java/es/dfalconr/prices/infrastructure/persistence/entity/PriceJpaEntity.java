package es.dfalconr.prices.infrastructure.persistence.entity;

import es.dfalconr.prices.domain.model.Price;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRICES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BRAND_ID", nullable = false)
    private Long brandId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "PRICE_LIST", nullable = false)
    private Integer priceList;

    @Column(name = "START_DATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "PRIORITY", nullable = false)
    private Integer priority;

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "CURR", nullable = false, length = 3)
    private String currency;

    @Column(name = "LAST_UPDATE")
    private LocalDateTime lastUpdate;

    @Column(name = "LAST_UPDATE_BY", length = 50)
    private String lastUpdateBy;

    public Price toDomain() {
        return new Price(
            this.id,
            this.brandId,
            this.productId,
            this.priceList,
            this.startDate,
            this.endDate,
            this.priority,
            this.price,
            this.currency
        );
    }
}
