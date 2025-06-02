package com.matoni.pricehub.article.repository;

import com.matoni.pricehub.article.entity.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Article upsertArticle(Article article) {
    // 🔍 Why native query?
    // Using a native SQL query because JPA does not support PostgreSQL's `ON CONFLICT DO UPDATE`,
    // which is required for atomic upsert behavior. This avoids race conditions during parallel
    // imports (e.g., from multiple CSV files) by ensuring that insert-or-update happens in a
    // single,
    // database-level operation. The use of `IS DISTINCT FROM` prevents unnecessary updates, and
    // `RETURNING id` allows us to immediately get the article's ID without an additional SELECT.
    //
    // This native query efficiently handles:
    // - inserting a new article if it doesn't exist
    // - updating the article if it exists and fields have changed (based on IS DISTINCT FROM)
    // - returning the ID of the affected article in both cases (insert or update)
    String upsertSql =
        """
      INSERT INTO articles (product_code, name, barcode, brand, unit)
      VALUES (:productCode, :name, :barcode, :brand, :unit)
      ON CONFLICT (product_code) DO UPDATE
      SET name = EXCLUDED.name,
          barcode = EXCLUDED.barcode,
          brand = EXCLUDED.brand,
          unit = EXCLUDED.unit,
          updated_at = now()
      WHERE articles.name IS DISTINCT FROM EXCLUDED.name
         OR articles.barcode IS DISTINCT FROM EXCLUDED.barcode
         OR articles.brand IS DISTINCT FROM EXCLUDED.brand
         OR articles.unit IS DISTINCT FROM EXCLUDED.unit
      RETURNING id
  """;

    List<?> result =
        entityManager
            .createNativeQuery(upsertSql)
            .setParameter("productCode", article.getProductCode())
            .setParameter("name", article.getName())
            .setParameter("barcode", article.getBarcode())
            .setParameter("brand", article.getBrand())
            .setParameter("unit", article.getUnit())
            .getResultList();

    Long id;
    if (!result.isEmpty()) {
      id = ((Number) result.get(0)).longValue();
    } else {
      // 🩹 Fallback: sometimes ON CONFLICT DO UPDATE does not execute if no values changed.
      // In that case, RETURNING returns no rows — so we manually fetch the existing ID.
      String selectSql = "SELECT id FROM articles WHERE product_code = :productCode";
      List<?> selectResult =
          entityManager
              .createNativeQuery(selectSql)
              .setParameter("productCode", article.getProductCode())
              .getResultList();

      if (selectResult.isEmpty()) {
        throw new IllegalStateException(
            "Upsert did not return article ID and fallback SELECT failed for: " + article);
      }

      id = ((Number) selectResult.get(0)).longValue();
    }

    article.setId(id);
    return article;
  }
}
