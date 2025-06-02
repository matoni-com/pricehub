package com.matoni.pricehub.price.file.service;

import com.matoni.pricehub.price.file.entity.ProcessedFile;
import com.matoni.pricehub.price.file.repository.ProcessedFileRepository;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import java.io.File;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceFileService {

  private final ProcessedFileRepository processedFileRepository;

  public void markFileAsProcessed(File csv, RetailChain retailChain) {
    ProcessedFile processedFile = new ProcessedFile();
    processedFile.setRetailChain(retailChain);
    processedFile.setFileName(csv.getName());
    processedFile.setProcessedAt(LocalDateTime.now());

    processedFileRepository.save(processedFile);
  }
}
