package com.matoni.pricehub.retailchain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "retail_chains")
@Getter
@Setter
public class RetailChain {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
}
