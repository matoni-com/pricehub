package com.matoni.pricehub.price.repository;

import com.matoni.pricehub.price.entity.PriceEntry;
import java.util.List;

public interface CustomPriceEntryRepository {
  void upsertPriceEntry(PriceEntry entry);

  void upsertAll(List<PriceEntry> entries);
}
