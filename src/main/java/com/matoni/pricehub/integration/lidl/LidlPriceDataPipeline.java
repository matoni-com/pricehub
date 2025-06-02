package com.matoni.pricehub.integration.lidl;

import com.matoni.pricehub.integration.common.ZipExtractor;
import com.matoni.pricehub.price.file.service.PriceFileDownloader;
import com.matoni.pricehub.price.file.service.ZipUnpackingFileDownloader;
import com.matoni.pricehub.price.service.PriceDataPipeline;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.repository.RetailChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LidlPriceDataPipeline {

  private final RetailChainRepository retailChainRepository;
  private final LidlCsvFileDownloadService lidlFileDownloadService;
  private final ZipExtractor zipExtractor;
  private final PriceDataPipeline priceDataPipeline;

  public void run() throws Exception {
    RetailChain lidl =
        retailChainRepository
            .findByName("Lidl")
            .orElseThrow(() -> new IllegalStateException("Retail chain Lidl not found"));

    PriceFileDownloader wrappedDownloader =
        new ZipUnpackingFileDownloader(lidlFileDownloadService, zipExtractor);

    priceDataPipeline.run(lidl, wrappedDownloader, "lidl");
  }
}
