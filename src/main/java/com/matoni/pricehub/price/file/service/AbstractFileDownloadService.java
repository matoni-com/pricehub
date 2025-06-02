package com.matoni.pricehub.price.file.service;

import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Slf4j
public abstract class AbstractFileDownloadService {

  protected final WebClient webClient;
  protected final String downloadDir;
  protected final Scheduler downloadScheduler;

  public AbstractFileDownloadService(WebClient webClient, String downloadDir) {
    this.webClient = webClient;
    this.downloadDir = downloadDir;
    this.downloadScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));
  }

  public List<Path> downloadFiles(Set<String> alreadyProcessed) throws Exception {
    Files.createDirectories(Paths.get(downloadDir));
    log.info("Ensured download directory exists: {}", downloadDir);

    List<String> urls = getFileUrls(alreadyProcessed);

    List<String> filteredUrls =
        urls.stream()
            .filter(
                url -> {
                  try {
                    String fileName = Paths.get(new URL(url).getPath()).getFileName().toString();
                    return !alreadyProcessed.contains(fileName);
                  } catch (Exception e) {
                    log.warn("Skipping malformed URL: {}", url, e);
                    return false;
                  }
                })
            .toList();

    if (filteredUrls.isEmpty()) {
      log.info("No new files to download.");
      return List.of();
    }

    return Flux.fromIterable(filteredUrls).flatMap(this::downloadFile, 4).collectList().block();
  }

  private Mono<Path> downloadFile(String fileUrl) {
    return Mono.fromCallable(
            () -> {
              String filename = Paths.get(new URL(fileUrl).getPath()).getFileName().toString();
              Path tempPath = Paths.get(downloadDir, filename + ".part");
              Path targetPath = Paths.get(downloadDir, filename);
              return new FileDownloadInfo(fileUrl, tempPath, targetPath);
            })
        .flatMap(this::downloadWithRetry)
        .subscribeOn(downloadScheduler);
  }

  private Mono<Path> downloadWithRetry(FileDownloadInfo info) {
    Flux<DataBuffer> dataBufferFlux =
        webClient
            .get()
            .uri(info.url())
            .retrieve()
            .bodyToFlux(DataBuffer.class)
            .timeout(Duration.ofSeconds(60));

    return writeToDisk(info, dataBufferFlux)
        .thenReturn(info.targetPath())
        .retryWhen(
            Retry.backoff(3, Duration.ofSeconds(2))
                .filter(ex -> ex instanceof java.io.IOException)
                .onRetryExhaustedThrow((spec, signal) -> signal.failure())
                .doBeforeRetry(
                    signal -> {
                      log.warn(
                          "Retrying {} (attempt {}): {}",
                          info.url(),
                          signal.totalRetries() + 1,
                          signal.failure());
                      try {
                        Files.deleteIfExists(info.tempPath());
                      } catch (Exception e) {
                        log.warn("Could not delete temp file {}", info.tempPath(), e);
                      }
                    }));
  }

  protected abstract List<String> getFileUrls(Set<String> alreadyProcessed) throws Exception;

  protected abstract Mono<Void> writeToDisk(FileDownloadInfo info, Flux<DataBuffer> data);

  protected record FileDownloadInfo(String url, Path tempPath, Path targetPath) {}
}
