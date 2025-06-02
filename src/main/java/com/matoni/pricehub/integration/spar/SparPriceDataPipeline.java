package com.matoni.pricehub.integration.spar;

import com.matoni.pricehub.price.file.entity.ProcessedFile;
import com.matoni.pricehub.price.file.repository.ProcessedFileRepository;
import com.matoni.pricehub.price.file.service.PriceFileService;
import com.matoni.pricehub.price.service.PriceImportService;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.repository.RetailChainRepository;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SparPriceDataPipeline {

  private final SparCsvFileDownloadService downloadService;
  private final PriceImportService priceImportService;
  private final RetailChainRepository retailChainRepository;
  private final ProcessedFileRepository processedFileRepository;
  private final PriceFileService priceFileService;

  /**
   * Entry point of the SPAR price data pipeline. This method performs the full ETL process: 1.
   * Finds the 'Spar' RetailChain entity. 2. Downloads all available CSV files. 3. Parses each CSV
   * and saves prices to the database.
   */
  public void run() throws Exception {
    RetailChain spar =
        retailChainRepository
            .findByName("Spar")
            .orElseThrow(() -> new IllegalStateException("Retail chain Spar not found"));

    Set<String> processedCsvNames =
        processedFileRepository.findByRetailChain(spar).stream()
            .map(ProcessedFile::getFileName)
            .collect(Collectors.toUnmodifiableSet());

    List<Path> csvFiles = downloadService.downloadFiles(processedCsvNames);

    ExecutorService executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    List<CompletableFuture<Void>> futures =
        csvFiles.stream()
            .map(
                csvPath ->
                    CompletableFuture.runAsync(
                        () -> {
                          try {
                            File csvFile = csvPath.toFile();
                            log.info("📂 Importing SPAR CSV file: {}", csvFile.getName());
                            priceImportService.importFromCsv(csvFile, spar, "spar");

                            priceFileService.markFileAsProcessed(csvFile, spar);
                            log.info("✅ Marked {} as processed", csvFile.getName());
                          } catch (Exception e) {
                            log.error(
                                "❌ Failed to import SPAR CSV {}: {}",
                                csvPath.getFileName(),
                                e.getMessage(),
                                e);
                          }
                        },
                        executor))
            .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    executor.shutdown();

    log.info("✅ SPAR price pipeline completed.");
  }
}
