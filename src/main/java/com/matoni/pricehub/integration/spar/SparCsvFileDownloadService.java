package com.matoni.pricehub.integration.spar;

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

@Slf4j
@Service
public class SparCsvFileDownloadService extends AbstractFileDownloadService
    implements PriceFileDownloader {

  private final SparCsvLinkParser linkParser;
  private final FileWriter fileWriter;

  public SparCsvFileDownloadService(
      WebClient webClient,
      @Value("${spar.csv.download-dir:downloads/spar}") String downloadDir,
      SparCsvLinkParser linkParser,
      FileWriter fileWriter) {
    super(webClient, downloadDir);
    this.linkParser = linkParser;
    this.fileWriter = fileWriter;
  }

  @Override
  protected List<String> getFileUrls() throws Exception {
    return linkParser.findCsvFileUrls();
  }

  @Override
  protected Mono<Void> writeToDisk(FileDownloadInfo info, Flux<DataBuffer> data) {
    return fileWriter.writeToTempThenMove(data, info.tempPath(), info.targetPath());
  }
}
