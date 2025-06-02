package com.matoni.pricehub.article.service;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.article.repository.ArticleRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

  public List<Article> findOrCreateAll(List<Article> inputArticles) {
    Map<String, Article> byProductCode =
        inputArticles.stream()
            .collect(Collectors.toMap(Article::getProductCode, Function.identity(), (a, b) -> a));

    // Step 1: Fetch existing articles
    List<Article> existing = articleRepository.findAllByProductCodeIn(byProductCode.keySet());

    Map<String, Article> result = new HashMap<>();
    for (Article a : existing) {
      result.put(a.getProductCode(), a);
      byProductCode.remove(a.getProductCode());
    }

    // Step 2: Insert missing ones
    for (Article newArticle : byProductCode.values()) {
      Article saved = articleRepository.upsertArticle(newArticle);
      result.put(saved.getProductCode(), saved);
    }

    return new ArrayList<>(result.values());
  }
}
