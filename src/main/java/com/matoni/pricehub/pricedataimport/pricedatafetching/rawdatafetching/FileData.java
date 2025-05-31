package com.matoni.pricehub.pricedataimport.pricedatafetching.rawdatafetching;


import lombok.Value;

@Value
public class FileData extends RawPriceData {
    String rawContent;
    String fileName;
}
