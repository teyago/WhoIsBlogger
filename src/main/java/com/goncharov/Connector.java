package com.goncharov;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Connector {

    public Document getDocument(String URL) {
        String USERAGENT = "Dalvik/2.1.0 (Linux; U; Android 9; ADT-2 Build/PTT5.181126.002)";
        try {
            return Jsoup
                    .connect(URL)
                    .userAgent(USERAGENT)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}