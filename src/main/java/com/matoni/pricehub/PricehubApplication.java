package com.matoni.pricehub;

import com.matoni.pricehub.integration.lidl.CsvFileDownloadService;
import java.util.Set;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableScheduling
public class PricehubApplication {

  private CsvFileDownloadService csvFileDownloadService;

  public static void main(String[] args) {
    SpringApplication.run(PricehubApplication.class, args);
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name)
      throws Exception {
    csvFileDownloadService.downloadZipFiles(Set.of());
    return String.format("Hello %s!", name);
  }
}
