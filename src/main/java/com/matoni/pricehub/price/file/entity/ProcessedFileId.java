package com.matoni.pricehub.price.file.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProcessedFileId implements Serializable {

  private Long retailChain;
  private String fileName;

  public ProcessedFileId() {}

  public ProcessedFileId(Long retailChain, String fileName) {
    this.retailChain = retailChain;
    this.fileName = fileName;
  }

  // required for @IdClass
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProcessedFileId)) return false;
    ProcessedFileId that = (ProcessedFileId) o;
    return Objects.equals(retailChain, that.retailChain) && Objects.equals(fileName, that.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(retailChain, fileName);
  }

  // getters/setters (or use Lombok if you prefer)
}
