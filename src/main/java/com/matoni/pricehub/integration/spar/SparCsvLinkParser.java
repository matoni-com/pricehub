package com.matoni.pricehub.integration.spar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SparCsvLinkParser {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final String BASE_URL = "https://www.spar.hr/datoteke_cjenici/Cjenik";

  private final ObjectMapper objectMapper = new ObjectMapper();

  public List<String> findCsvFileUrls(LocalDate date) throws Exception {
    String jsonUrl = BASE_URL + DATE_FORMAT.format(date) + ".json";

    SparPriceListFileList fileList =
        objectMapper.readValue(new URL(jsonUrl), SparPriceListFileList.class);

    return fileList.getFiles().stream()
        .map(SparFile::getUrl)
        .filter(url -> url != null && !url.isBlank())
        .collect(Collectors.toList());
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class SparPriceListFileList {
    private List<SparFile> files;

    public List<SparFile> getFiles() {
      return files;
    }

    public void setFiles(List<SparFile> files) {
      this.files = files;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class SparFile {
    private String name;

    @JsonProperty("URL")
    private String url;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }
}
