package com.matoni.pricehub.pricedataimport.pricedatafetching.rawdataparsing;

import com.matoni.pricehub.pricedataimport.pricedatafetching.PriceDatum;
import com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching.RawPriceData;

import java.util.List;

public abstract class RawPriceDataParser<T extends RawPriceData> {
    public abstract List<PriceDatum> parse(T rawPriceData);
}
