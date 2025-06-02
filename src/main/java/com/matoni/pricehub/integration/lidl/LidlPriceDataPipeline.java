package com.matoni.pricehub.integration.lidl;

import com.matoni.pricehub.price.file.entity.ProcessedFile;
import com.matoni.pricehub.price.file.repository.ProcessedFileRepository;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.repository.RetailChainRepository;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LidlPriceDataPipeline {

  private final CsvFileDownloadService downloadService;
  private final ZipExtractor zipExtractor;
  private final PriceImportService priceImportService;
  private final RetailChainRepository retailChainRepository;
  private final ProcessedFileRepository processedFileRepository;

  /**
   * Entry point of the Lidl price data pipeline. This method performs the full ETL process: 1.
   * Finds the 'Lidl' RetailChain entity. 2. Downloads all available ZIP files with price data. 3.
   * Extracts CSV files from each ZIP archive. 4. Parses each CSV and saves prices to the database.
   */
  public void run() throws Exception {
    RetailChain lidl =
        retailChainRepository
            .findByName("Lidl")
            .orElseThrow(() -> new IllegalStateException("Retail chain Lidl not found"));

    Set<String> processedZipNames =
        processedFileRepository.findByRetailChain(lidl).stream()
            .map(ProcessedFile::getFileName)
            .collect(Collectors.toUnmodifiableSet());

    List<Path> zipFiles = downloadService.downloadZipFiles(processedZipNames);

    for (Path zipPath : zipFiles) {
      try {
        List<File> csvFiles = zipExtractor.extractCsvFiles(zipPath.toFile(), zipPath.getParent());
        log.info("📂 Extracted {} file(s) from {}", csvFiles.size(), zipPath.getFileName());

        csvFiles.parallelStream()
            .forEach(
                csv -> {
                  try {
                    log.info("📂 Importing {} file", csv.getName());
                    priceImportService.importFromLidlCsv(csv, lidl);
                  } catch (Exception e) {
                    log.error("❌ Failed to import CSV {}: {}", csv.getName(), e.getMessage(), e);
                  }
                });

      } catch (Exception e) {
        log.error("❌ Failed to unzip file {}: {}", zipPath.getFileName(), e.getMessage(), e);
      }
    }
  }
}
