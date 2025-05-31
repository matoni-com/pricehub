package com.matoni.pricehub.pricedataimport.pricedatafetching;

import java.util.List;

public abstract class PriceDataFetcher {
    public abstract List<PriceDatum> fetch();
}
