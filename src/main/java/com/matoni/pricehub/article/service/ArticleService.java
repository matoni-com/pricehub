package com.matoni.pricehub.article.service;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.article.repository.ArticleRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;

  @Transactional
  public Article findOrCreate(
      String productCode, String name, String barcode, String brand, String unit) {
    Article article = new Article();
    article.setProductCode(productCode);
    article.setName(name);
    article.setBarcode(barcode);
    article.setBrand(brand);
    article.setUnit(unit);

    try {
      return articleRepository.upsertArticle(article);
    } catch (DataIntegrityViolationException | NoResultException e) {
      return articleRepository
          .findByProductCode(productCode)
          .orElseThrow(
              () ->
                  new IllegalStateException(
                      "Article upsert failed and no existing entry found for productCode="
                          + productCode,
                      e));
    }
  }
}
