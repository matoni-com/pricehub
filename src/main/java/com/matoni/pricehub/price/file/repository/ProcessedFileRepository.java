package com.matoni.pricehub.price.file.repository;

import com.matoni.pricehub.price.file.entity.ProcessedFile;
import com.matoni.pricehub.price.file.entity.ProcessedFileId;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedFileRepository extends JpaRepository<ProcessedFile, ProcessedFileId> {
  boolean existsById(ProcessedFileId id);

  List<ProcessedFile> findByRetailChain(RetailChain retailChain);
}
