package com.matoni.pricehub.price.entity;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.retailchain.entity.store.Store;
import com.matoni.pricehub.utils.entity.TimestampedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "price_entries")
@Getter
@Setter
public class PriceEntry extends TimestampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne private Store store;

  @ManyToOne private Article article;

  private LocalDate priceDate;
  private BigDecimal retailPrice;
  private BigDecimal pricePerUnit;
  private BigDecimal anchorPrice;
}
