package com.matoni.pricehub.pricedataimport.pricedatafetching;

import com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching.RawPriceData;
import com.matoni.pricehub.pricedataimport.pricedatafetching.rawdataparsing.RawPriceDataParser;
import com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching.SingleSourceRawPriceDataFetcher;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TwoStageSingleSourcePriceDataFetcher<T extends RawPriceData> extends PriceDataFetcher {

    private SingleSourceRawPriceDataFetcher<T> dataFetcher;
    private RawPriceDataParser<T> parser;

    @Override
    public List<PriceDatum> fetch() {
        T rawPriceData = dataFetcher.fetch();

        return parser.parse(rawPriceData);
    }
}
