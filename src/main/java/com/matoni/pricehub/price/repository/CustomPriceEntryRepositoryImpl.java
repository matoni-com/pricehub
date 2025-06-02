package com.matoni.pricehub.price.repository;

import com.matoni.pricehub.price.entity.PriceEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CustomPriceEntryRepositoryImpl implements CustomPriceEntryRepository {

  @PersistenceContext private EntityManager entityManager;

  private static final int MAX_BATCH_SIZE = 1000;

  @Override
  @Transactional
  public void upsertPriceEntry(PriceEntry entry) {
    upsertAll(List.of(entry));
  }

  @Override
  @Transactional
  public void upsertAll(List<PriceEntry> entries) {
    if (entries.isEmpty()) return;

    // Deduplicate entries by conflict key to avoid Postgres double update error
    Map<String, PriceEntry> deduplicated =
        entries.stream()
            .collect(
                Collectors.toMap(
                    e ->
                        e.getStore().getId()
                            + "-"
                            + e.getArticle().getId()
                            + "-"
                            + e.getPriceDate(),
                    e -> e,
                    (existing, replacement) -> replacement));

    List<PriceEntry> deduplicatedList = new ArrayList<>(deduplicated.values());

    for (int i = 0; i < deduplicatedList.size(); i += MAX_BATCH_SIZE) {
      int end = Math.min(i + MAX_BATCH_SIZE, deduplicatedList.size());
      List<PriceEntry> batch = deduplicatedList.subList(i, end);
      upsertBatch(batch);
    }
  }

  private void upsertBatch(List<PriceEntry> entries) {
    // Using a native SQL query to perform bulk upserts efficiently with PostgreSQL's `ON CONFLICT`
    // clause.
    // This ensures atomic insert-or-update operations based on the unique constraint (store_id,
    // article_id, price_date).
    // JPA does not support batch upserts natively, and using native SQL prevents race conditions
    // when the same
    // product-store-date combination is updated concurrently. It also avoids multiple roundtrips to
    // the database
    // and ensures performance and data consistency during large CSV imports.
    String sql =
        """
            INSERT INTO price_entries (
                store_id, article_id, price_date, retail_price, price_per_unit, anchor_price, created_at, updated_at
            ) VALUES
            """
            + entries.stream()
                .map(e -> "(?, ?, ?, ?, ?, ?, now(), now())")
                .collect(Collectors.joining(", "))
            + """
        ON CONFLICT (store_id, article_id, price_date)
        DO UPDATE SET
            retail_price = EXCLUDED.retail_price,
            price_per_unit = EXCLUDED.price_per_unit,
            anchor_price = EXCLUDED.anchor_price,
            updated_at = now()
        """;

    Query query = entityManager.createNativeQuery(sql);

    int index = 1;
    for (PriceEntry e : entries) {
      query.setParameter(index++, e.getStore().getId());
      query.setParameter(index++, e.getArticle().getId());
      query.setParameter(index++, e.getPriceDate());
      query.setParameter(index++, e.getRetailPrice());
      query.setParameter(index++, e.getPricePerUnit());
      query.setParameter(index++, e.getAnchorPrice());
    }

    query.executeUpdate();
  }
}
