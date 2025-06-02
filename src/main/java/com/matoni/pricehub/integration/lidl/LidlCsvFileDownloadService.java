package com.matoni.pricehub.integration.lidl;

import com.matoni.pricehub.integration.common.FileWriter;
import com.matoni.pricehub.price.file.service.AbstractFileDownloadService;
import com.matoni.pricehub.price.file.service.PriceFileDownloader;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
The goal of this service is to:

1) Go to the Lidl price page,

2) Find all .zip file links,

3) Download those .zip files concurrently using a thread pool,

4) Retry downloads with backoff on failures,

5) Use a .part temporary file, and rename it once successful.
 */

@Slf4j
@Service
public class LidlCsvFileDownloadService extends AbstractFileDownloadService
    implements PriceFileDownloader {

  private final LidlZipLinkParser linkParser;
  private final FileWriter fileWriter;

  public LidlCsvFileDownloadService(
      WebClient webClient,
      @Value("${lidl.csv.download-dir:downloads}") String downloadDir,
      LidlZipLinkParser linkParser,
      FileWriter fileWriter) {
    super(webClient, downloadDir);
    this.linkParser = linkParser;
    this.fileWriter = fileWriter;
  }

  @Override
  protected List<String> getFileUrls() throws Exception {
    return linkParser.findZipFileUrls();
  }

  @Override
  protected Mono<Void> writeToDisk(FileDownloadInfo info, Flux<DataBuffer> data) {
    return fileWriter.writeToTempThenMove(data, info.tempPath(), info.targetPath());
  }
}
