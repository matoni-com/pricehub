package com.matoni.pricehub.integration.lidl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ZipFileWriter {

  public Mono<Void> writeToTempThenMove(Flux<DataBuffer> data, Path tempPath, Path targetPath) {
    log.info("📥 Starting write to temp file: {}", tempPath);
    return DataBufferUtils.write(data, tempPath, StandardOpenOption.CREATE)
        .doOnSuccess(
            unused -> {
              log.info("✅ Successfully wrote to temp file: {}", tempPath);
              moveFile(tempPath, targetPath);
            })
        .doOnError(
            e -> {
              log.error("❌ Error writing to temp file: {}", tempPath, e);
              deleteIfExists(tempPath);
            });
  }

  private void moveFile(Path from, Path to) {
    try {
      Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
      log.info("📦 Moved file from {} to {}", from, to);
    } catch (IOException e) {
      log.error("❌ Failed to move file from {} to {}", from, to, e);
      throw new UncheckedIOException("Failed to move file to: " + to, e);
    }
  }

  private void deleteIfExists(Path path) {
    try {
      Files.deleteIfExists(path);
      log.warn("🧹 Deleted temp file: {}", path);
    } catch (IOException e) {
      log.error("❌ Failed to delete temp file: {}", path, e);
    }
  }
}
