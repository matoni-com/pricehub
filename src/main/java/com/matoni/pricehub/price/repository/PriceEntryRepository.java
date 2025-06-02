package com.matoni.pricehub.price.repository;

import com.matoni.pricehub.price.entity.PriceEntry;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceEntryRepository
    extends JpaRepository<PriceEntry, Long>, CustomPriceEntryRepository {
  Optional<PriceEntry> findByStoreIdAndArticleIdAndPriceDate(
      Long storeId, Long articleId, LocalDate priceDate);
}
