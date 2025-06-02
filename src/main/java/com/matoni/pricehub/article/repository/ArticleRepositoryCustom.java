package com.matoni.pricehub.article.repository;

import com.matoni.pricehub.article.entity.Article;
import java.util.Collection;
import java.util.List;

public interface ArticleRepositoryCustom {
  Article upsertArticle(Article article);

  List<Article> findAllByProductCodeIn(Collection<String> productCodes);
}
