package com.matoni.pricehub.article.service;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.article.repository.ArticleRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;

  public Article findOrCreate(
      String productCode, String name, String barcode, String brand, String unit) {
    Optional<Article> existing = articleRepository.findByProductCode(productCode);
    return existing.orElseGet(
        () -> {
          Article article = new Article();
          article.setProductCode(productCode);
          article.setName(name);
          article.setBarcode(barcode);
          article.setBrand(brand);
          article.setUnit(unit);
          return articleRepository.save(article);
        });
  }
}
