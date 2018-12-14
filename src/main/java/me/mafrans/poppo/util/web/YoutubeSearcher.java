package me.mafrans.poppo.util.web;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.objects.YoutubeVideo;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class YoutubeSearcher {
    private final String baseUrl = "https://www.googleapis.com/youtube/v3/search?type=video&part=id,snippet&q=${query}&key=${token}&maxResults=${maxResults}";

    public YoutubeVideo[] GetVideos(String query, int amount) throws IOException, ParseException {
        String url = baseUrl.replace("${query}", query.replace(" ", "%20")).replace("${token}", Main.config.google_token).replace("${maxResults}", String.valueOf(amount));

        System.out.println(HTTPUtil.GET(url, new HashMap<>()));
        JSONObject jsonObject = new JSONObject(HTTPUtil.GET(url, new HashMap<>()));
        if(!jsonObject.has("items")) {
            return null;
        }
        if(jsonObject.getJSONArray("items").length() == 0) {
            return new YoutubeVideo[0];
        }

        List<YoutubeVideo> outList = new ArrayList<>();

        for(int i = 0; i < jsonObject.getJSONArray("items").length(); i++) {
            JSONObject youtubeObject = jsonObject.getJSONArray("items").getJSONObject(i);
            YoutubeVideo video = new YoutubeVideo();
            video.setVideoId(youtubeObject.getJSONObject("id").getString("videoId"));
            video.setChannelId(youtubeObject.getJSONObject("snippet").getString("channelId"));
            video.setChannelTitle(youtubeObject.getJSONObject("snippet").getString("channelTitle"));
            video.setDescription(youtubeObject.getJSONObject("snippet").getString("description"));
            video.setTitle(youtubeObject.getJSONObject("snippet").getString("title"));

            String[] thumbnails = new String[3];
            thumbnails[0] = youtubeObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");
            thumbnails[1] = youtubeObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
            thumbnails[2] = youtubeObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");

            video.setImages(thumbnails);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS");
            Date published = dateFormat.parse(youtubeObject.getJSONObject("snippet").getString("publishedAt").replaceFirst("T", "").replaceFirst("Z", ""));
            video.setPublished(published);

            outList.add(video);
        }

        return outList.toArray(new YoutubeVideo[0]);
    }
}
