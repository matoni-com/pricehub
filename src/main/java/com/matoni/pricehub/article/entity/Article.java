package com.matoni.pricehub.article.entity;

import com.matoni.pricehub.utils.entity.TimestampedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articles")
@Getter
@Setter
public class Article extends TimestampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String productCode;
  private String barcode;
  private String brand;
  private String unit;
}
