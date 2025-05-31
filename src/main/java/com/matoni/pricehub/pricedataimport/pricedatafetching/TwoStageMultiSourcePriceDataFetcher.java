package com.matoni.pricehub.pricedataimport.pricedatafetching;

import com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching.MultiSourceRawPriceDataFetcher;
import com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching.RawPriceData;
import com.matoni.pricehub.pricedataimport.pricedatafetching.rawdataparsing.RawPriceDataParser;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TwoStageMultiSourcePriceDataFetcher<T extends RawPriceData> extends PriceDataFetcher {

    private MultiSourceRawPriceDataFetcher<T> dataFetcher;
    private RawPriceDataParser<T> parser;


    @Override
    public List<PriceDatum> fetch() {
        List<T> rawPriceDataList = dataFetcher.fetch();

        return rawPriceDataList.stream().flatMap(rawData -> parser.parse(rawData).stream()).toList();
    }
}
