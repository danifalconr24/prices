package es.dfalconr.prices.infrastructure.persistence.adapter;

import es.dfalconr.prices.domain.model.Price;
import es.dfalconr.prices.domain.port.PriceRepository;
import es.dfalconr.prices.infrastructure.persistence.entity.PriceJpaEntity;
import es.dfalconr.prices.infrastructure.persistence.repository.PriceJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PriceRepositoryAdapter implements PriceRepository {

    private final PriceJpaRepository jpaRepository;

    public PriceRepositoryAdapter(PriceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Price> findApplicablePrices(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
    ) {
        return jpaRepository.findApplicablePrices(applicationDate, productId, brandId)
            .stream()
            .map(PriceJpaEntity::toDomain)
            .toList();
    }
}
