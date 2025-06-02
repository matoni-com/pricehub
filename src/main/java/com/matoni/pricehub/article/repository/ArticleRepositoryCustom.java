package com.matoni.pricehub.article.repository;

import com.matoni.pricehub.article.entity.Article;

public interface ArticleRepositoryCustom {
  Article upsertArticle(Article article);
}
