package com.matoni.pricehub.article.repository;

import com.matoni.pricehub.article.entity.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
  Optional<Article> findByProductCode(String productCode);
}
