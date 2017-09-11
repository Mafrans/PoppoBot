package me.mafrans.poppo.util;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InformationGetter {
    public static Information getFirstHit(String query) {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");

            url.put("query", query);
            url.put("limit", "1");
            url.put("indent", "false");
            url.put("key", ConfigEntry.GOOGLE_TOKEN.getString());

            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();

            JSONObject object = new JSONObject(httpResponse.parseAsString());

            return Information.parseFromJSON(object.getJSONArray("itemListElement").getJSONObject(0).getJSONObject("result"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Information> getHits(String query) {
        try {
            List<Information> outList = new ArrayList<>();

            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");

            url.put("query", query);
            url.put("limit", "9");
            url.put("indent", "false");
            url.put("key", ConfigEntry.GOOGLE_TOKEN.getString());

            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();

            JSONObject object = new JSONObject(httpResponse.parseAsString());

            for(int i = 0; i < (object.getJSONArray("itemListElement").length() > 10 ? 10 : object.getJSONArray("itemListElement").length()); i++) {
                outList.add(Information.parseFromJSON(object.getJSONArray("itemListElement").getJSONObject(i).getJSONObject("result")));
            }

            return outList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}