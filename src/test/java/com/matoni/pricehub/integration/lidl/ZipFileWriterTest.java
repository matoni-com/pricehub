package com.matoni.pricehub.integration.lidl;

import static org.assertj.core.api.Assertions.assertThat;

import com.matoni.pricehub.integration.common.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ZipFileWriterTest {

  private FileWriter zipFileWriter;
  private Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    zipFileWriter = new FileWriter();
    tempDir = Files.createTempDirectory("zip-test");
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.walk(tempDir).map(Path::toFile).sorted((a, b) -> -a.compareTo(b)).forEach(File::delete);
  }

  @Test
  void shouldWriteAndMoveFile() {
    Path tempPath = tempDir.resolve("test.zip.part");
    Path targetPath = tempDir.resolve("test.zip");

    DataBuffer buffer = new DefaultDataBufferFactory().wrap("test-content".getBytes());
    Flux<DataBuffer> data = Flux.just(buffer);

    StepVerifier.create(zipFileWriter.writeToTempThenMove(data, tempPath, targetPath))
        .verifyComplete();

    assertThat(Files.exists(targetPath)).isTrue();
    assertThat(Files.notExists(tempPath)).isTrue();
  }

  @Test
  void shouldDeleteTempFileOnError() {
    Path tempPath = tempDir.resolve("error.zip.part");
    Path targetPath = tempDir.resolve("error.zip");

    Flux<DataBuffer> failingData = Flux.error(new IOException("Simulated download failure"));

    StepVerifier.create(zipFileWriter.writeToTempThenMove(failingData, tempPath, targetPath))
        .expectError()
        .verify();

    assertThat(Files.notExists(tempPath)).isTrue();
    assertThat(Files.notExists(targetPath)).isTrue();
  }
}
