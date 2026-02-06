package es.dfalconr.prices.infrastructure.persistence.repository;

import es.dfalconr.prices.infrastructure.persistence.entity.PriceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceJpaRepository extends JpaRepository<PriceJpaEntity, Long> {

    @Query("""
        SELECT p FROM PriceJpaEntity p
        WHERE p.brandId = :brandId
        AND p.productId = :productId
        AND p.startDate <= :applicationDate
        AND p.endDate >= :applicationDate
        ORDER BY p.priority DESC
        """)
    List<PriceJpaEntity> findApplicablePrices(
        @Param("applicationDate") LocalDateTime applicationDate,
        @Param("productId") Long productId,
        @Param("brandId") Long brandId
    );
}
