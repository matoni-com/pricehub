package com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching;

import java.util.List;

public abstract class MultiSourceRawPriceDataFetcher<T extends RawPriceData> {
    public abstract List<T> fetch();
}
