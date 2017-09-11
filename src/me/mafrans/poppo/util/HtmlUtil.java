package me.mafrans.poppo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class HtmlUtil {
    public static String getRawText(String url) throws IOException {
        Document doc = Jsoup.connect(url).ignoreContentType(true).timeout(10000).get();
        return doc.body().text();
    }
}
