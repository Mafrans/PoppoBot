package me.mafrans.poppo.util.web;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeSearcher {
    private final String searchBaseUrl = "https://www.googleapis.com/youtube/v3/search?type=video&part=id,snippet&q=${query}&key=${token}&maxResults=${maxResults}";
    private final String videoBaseUrl = "https://www.googleapis.com/youtube/v3/videos?type=video&part=id,snippet&id=${id}&key=${token}&maxResults=${maxResults}";

    public YoutubeVideo[] GetVideos(String query, int amount) throws IOException, ParseException {
        String url = searchBaseUrl.replace("${query}", query.replace(" ", "%20")).replace("${token}", Main.config.google_token).replace("${maxResults}", String.valueOf(amount));

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
            outList.add(parseVideo(youtubeObject));
        }

        return outList.toArray(new YoutubeVideo[0]);
    }

    public YoutubeVideo getVideo(String url) throws IOException, ParseException {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (!matcher.find()) {
            return null;
        }

        String id = matcher.group();
        String videoUrl = videoBaseUrl.replace("${id}", id).replace("${token}", Main.config.google_token).replace("${maxResults}", "1");
        JSONObject jsonObject = new JSONObject(HTTPUtil.GET(videoUrl, new HashMap<>()));

        if(!jsonObject.has("items")) {
            return null;
        }
        if(jsonObject.getJSONArray("items").length() == 0) {
            return null;
        }

        return parseVideo(jsonObject.getJSONArray("items").getJSONObject(0));
    }

    public YoutubeVideo parseVideo(JSONObject youtubeObject) throws ParseException {
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
        return video;
    }
}
