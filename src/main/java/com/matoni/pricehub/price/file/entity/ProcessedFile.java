package com.matoni.pricehub.price.file.entity;

import com.matoni.pricehub.retailchain.entity.RetailChain;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "processed_files")
@IdClass(ProcessedFileId.class)
@Getter
@Setter
public class ProcessedFile {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "retail_chain_id")
  private RetailChain retailChain;

  @Id private String fileName;

  private LocalDateTime processedAt = LocalDateTime.now();
}
