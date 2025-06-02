package com.matoni.pricehub.integration.spar;

import static org.assertj.core.api.Assertions.assertThat;

import com.matoni.pricehub.common.BaseIntegrationSuite;
import com.matoni.pricehub.price.file.entity.ProcessedFile;
import com.matoni.pricehub.price.file.repository.ProcessedFileRepository;
import com.matoni.pricehub.price.repository.PriceEntryRepository;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.repository.RetailChainRepository;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PriceDataPipelineSparIntegrationTest extends BaseIntegrationSuite {

  @Autowired private SparPriceDataPipeline sparPriceDataPipeline;
  @Autowired private PriceEntryRepository priceEntryRepository;
  @Autowired private RetailChainRepository retailChainRepository;
  @Autowired private ProcessedFileRepository processedFileRepository;

  private static final String DOWNLOAD_DIR = "downloads";

  @BeforeEach
  void ensureSparRetailChainExists() {
    retailChainRepository
        .findByName("Spar")
        .orElseGet(
            () -> {
              RetailChain spar = new RetailChain();
              spar.setName("Spar");
              return retailChainRepository.save(spar);
            });
  }

  @AfterEach
  void cleanUp() throws Exception {
    if (Files.exists(Paths.get(DOWNLOAD_DIR))) {
      Files.walk(Paths.get(DOWNLOAD_DIR))
          .map(Path::toFile)
          .sorted((f1, f2) -> -f1.compareTo(f2)) // delete children first
          .forEach(File::delete);
    }
  }

  @Test
  void it_should_download_and_import_prices_for_spar() throws Exception {
    RetailChain spar = retailChainRepository.findByName("Spar").orElseThrow();

    Set<String> processedCsvNames =
        Set.of(
            "Supermarket 200_Ulica Proštinske bune_20_52100_Pula_1_02.06.2025_7.15h.csv",
            "Supermarket 201_Poreč_03.06.2025_7.15h.csv" // Add more if needed
            );

    List<ProcessedFile> filesToSeed =
        processedCsvNames.stream()
            .map(
                name -> {
                  ProcessedFile pf = new ProcessedFile();
                  pf.setRetailChain(spar);
                  pf.setFileName(name);
                  return pf;
                })
            .toList();

    processedFileRepository.saveAll(filesToSeed);

    // when
    sparPriceDataPipeline.run();

    // then
    File downloadDir = new File(DOWNLOAD_DIR);
    assertThat(downloadDir.exists()).isTrue();

    File[] allFiles = downloadDir.listFiles();
    assertThat(allFiles).isNotEmpty();
    assertThat(allFiles).anyMatch(f -> f.getName().endsWith(".csv"));

    long count = priceEntryRepository.count();
    assertThat(count).isGreaterThan(0);
  }
}
