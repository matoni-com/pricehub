package com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching;

public abstract class SingleSourceRawPriceDataFetcher<T extends RawPriceData> {
    public abstract T fetch();
}
