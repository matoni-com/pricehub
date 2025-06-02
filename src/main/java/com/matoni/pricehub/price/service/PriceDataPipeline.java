package com.matoni.pricehub.price.service;

import com.matoni.pricehub.price.file.entity.ProcessedFile;
import com.matoni.pricehub.price.file.repository.ProcessedFileRepository;
import com.matoni.pricehub.price.file.service.PriceFileDownloader;
import com.matoni.pricehub.price.file.service.PriceFileService;
import com.matoni.pricehub.retailchain.entity.RetailChain;
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
public class PriceDataPipeline {

  private final ProcessedFileRepository processedFileRepository;
  private final PriceImportService priceImportService;
  private final PriceFileService priceFileService;

  /**
   * Runs a generic price data pipeline for the given retailer and file downloader.
   *
   * @param retailChain the retail chain entity (e.g. Lidl, Spar)
   * @param fileDownloader implementation that downloads relevant files
   * @param parserStrategy strategy string used inside PriceImportService
   */
  public void run(
      RetailChain retailChain, PriceFileDownloader fileDownloader, String parserStrategy)
      throws Exception {

    log.info("🚀 Starting price import pipeline for {}", retailChain.getName());

    Set<String> alreadyProcessed =
        processedFileRepository.findByRetailChain(retailChain).stream()
            .map(ProcessedFile::getFileName)
            .collect(Collectors.toUnmodifiableSet());

    List<Path> newFiles = fileDownloader.downloadFiles(alreadyProcessed);

    if (newFiles.isEmpty()) {
      log.info("✅ No new files to import for {}", retailChain.getName());
      return;
    }

    ExecutorService executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    List<CompletableFuture<Void>> futures =
        newFiles.stream()
            .map(
                path ->
                    CompletableFuture.runAsync(
                        () -> {
                          File file = path.toFile();
                          try {
                            log.info("📥 Importing file: {}", file.getName());
                            priceImportService.importFromCsv(file, retailChain, parserStrategy);
                            priceFileService.markFileAsProcessed(file, retailChain);
                            log.info("✅ Processed and saved file: {}", file.getName());
                          } catch (Exception e) {
                            log.error(
                                "❌ Failed to import file {}: {}",
                                file.getName(),
                                e.getMessage(),
                                e);
                          }
                        },
                        executor))
            .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    executor.shutdown();

    log.info("🏁 Completed price import pipeline for {}", retailChain.getName());
  }
}
