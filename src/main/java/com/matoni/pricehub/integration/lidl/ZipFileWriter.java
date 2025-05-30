package com.matoni.pricehub.integration.lidl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ZipFileWriter {

  public Mono<Void> writeToTempThenMove(Flux<DataBuffer> data, Path tempPath, Path targetPath) {
    return DataBufferUtils.write(data, tempPath, StandardOpenOption.CREATE)
        .doOnSuccess(unused -> moveFile(tempPath, targetPath))
        .doOnError(e -> deleteIfExists(tempPath));
  }

  private void moveFile(Path from, Path to) {
    try {
      Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to move file to: " + to, e);
    }
  }

  private void deleteIfExists(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException ignored) {
    }
  }
}
