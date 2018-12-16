package me.mafrans.poppo.util.web;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HTTPUtil {
    private static final String USER_AGENT = "Mozilla/5.0"; // Use Mozilla as a user agent

    public static String GET(String url, Map<String, String> header) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        // Default is GET
        connection.setRequestMethod("GET");

        //add request header
        connection.setRequestProperty("User-Agent", USER_AGENT);
        for(String key : header.keySet()) {
            connection.setRequestProperty(key, header.get(key));
        }

        int responseCode = connection.getResponseCode();
        if(!(responseCode >= 200 && responseCode < 300)) { // Something went wrong
            return String.valueOf(responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static String POST(String url, Map<String, String> parameters, Map<String, String> header) throws IOException {

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        StringBuilder urlParameters = new StringBuilder();
        boolean first = true;
        for(String key : parameters.keySet()) {
            if(first) {
                urlParameters.append(key + "=" + parameters.get(key));
                first = false;
            }
            else {
                urlParameters.append("&" + key + "=" + parameters.get(key));
            }
        }

        wr.writeBytes(urlParameters.toString());
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        if(!(responseCode >= 200 && responseCode < 300)) { // Something went wrong
            return String.valueOf(responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();
    }

    public static String getRawText(String sUrl, Map<String, String> params) throws IOException {
        URL url = new URL(sUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(params));
        out.flush();
        out.close();

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }

    public static JSONObject getJSON(String sUrl, Map<String, String> params) throws IOException {
        StringBuilder urlParameters = new StringBuilder();

        boolean first = true;
        for(String key : params.keySet()) {
            if(first) {
                urlParameters.append(key + "=" + params.get(key));
                first = false;
            }
            else {
                urlParameters.append("&" + key + "=" + params.get(key));
            }
        }

        String response = GET(sUrl + "?" + urlParameters.toString(), new HashMap<>());
        return new JSONObject(response);
    }

    public static String getWikipediaThumbnail(String title) throws IOException {
        String baseUrl = "https://en.wikipedia.org/w/api.php?action=query&titles=" + title.replace(" ", "_") + "&prop=pageimages&format=json&pithumbsize=100";

        JSONObject jsonObject = getJSON(baseUrl, new HashMap<>());
        if(jsonObject.getJSONObject("query").getJSONObject("pages").length() == 0) {
            return null;
        }

        JSONObject firstMatch = jsonObject.getJSONObject("query").getJSONObject("pages").getJSONObject(jsonObject.getJSONObject("query").getJSONObject("pages").keySet().toArray(new String[0])[0]);
        if(!firstMatch.has("thumbnail")) {
            return null;
        }
        return firstMatch.getJSONObject("thumbnail").getString("source");
    }
}
