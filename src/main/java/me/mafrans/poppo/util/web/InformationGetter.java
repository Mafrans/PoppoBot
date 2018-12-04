package me.mafrans.poppo.util.web;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.objects.Information;
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
            url.put("key", Main.config.google_token);

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
            url.put("key", Main.config.google_token);

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