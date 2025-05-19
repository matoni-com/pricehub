package com.matoni.pricehub.integration.lidl;

import static org.assertj.core.api.Assertions.assertThat;

import com.matoni.pricehub.article.repository.ArticleRepository;
import com.matoni.pricehub.common.BaseIntegrationSuite;
import com.matoni.pricehub.price.repository.PriceEntryRepository;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.repository.RetailChainRepository;
import com.matoni.pricehub.retailchain.repository.store.StoreRepository;
import java.io.File;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PriceImporterTests extends BaseIntegrationSuite {

  @Autowired private PriceImporterService priceImporterService;
  @Autowired private RetailChainRepository retailChainRepository;
  @Autowired private PriceEntryRepository priceEntryRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private ArticleRepository articleRepository;

  @Test
  void it_should_import_lidl_csv_file() throws Exception {
    // given
    RetailChain lidl = new RetailChain();
    lidl.setName("Lidl");
    retailChainRepository.save(lidl);

    URL resource =
        getClass()
            .getClassLoader()
            .getResource(
                "csv/lidl/Supermarket 112_Duga ulica_111_32100_Vinkovci_1_19.05.2025_7.15h.csv");
    assertThat(resource).isNotNull();
    File file = new File(resource.toURI());

    // when
    priceImporterService.importFromLidlCsv(file, lidl);

    // then
    assertThat(priceEntryRepository.count()).isGreaterThan(0);
    assertThat(articleRepository.count()).isGreaterThan(0);
    assertThat(storeRepository.count()).isEqualTo(1);
  }
}
