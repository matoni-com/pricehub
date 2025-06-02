package com.matoni.pricehub.integration.spar;

import com.matoni.pricehub.price.file.service.PriceFileDownloader;
import com.matoni.pricehub.price.service.PriceDataPipeline;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.repository.RetailChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SparPriceDataPipeline {

  private final RetailChainRepository retailChainRepository;
  private final SparCsvFileDownloadService sparCsvFileDownloadService;
  private final PriceDataPipeline priceDataPipeline;

  public void run() throws Exception {
    RetailChain spar =
        retailChainRepository
            .findByName("Spar")
            .orElseThrow(() -> new IllegalStateException("Retail chain Spar not found"));

    // SparCsvFileDownloadService already returns extracted CSVs
    PriceFileDownloader directDownloader = sparCsvFileDownloadService;

    priceDataPipeline.run(spar, directDownloader, "spar");
  }
}
