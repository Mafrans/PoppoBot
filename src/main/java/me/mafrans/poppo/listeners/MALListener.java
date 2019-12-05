package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandHandler;
import me.mafrans.poppo.util.Feature;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Anime;
import me.mafrans.poppo.util.objects.Manga;
import me.mafrans.poppo.util.objects.Rank;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MALListener extends ListenerAdapter {
    private final String mangaExpression = "\\[\\[(.*?)]]";
    private final String animeExpression = "\\{\\{(.*?)}}";

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        if(event.getChannel().getType() == ChannelType.TEXT) {
            String msg = event.getMessage().getContentDisplay();

            Pattern mangaPattern = Pattern.compile(mangaExpression);
            Pattern animePattern = Pattern.compile(animeExpression);

            Matcher mangaMatcher = mangaPattern.matcher(msg);
            Matcher animeMatcher = animePattern.matcher(msg);

            String baseUrl = "https://api.jikan.moe/v3/search/";
            String type;

            Map<String, String> params = new HashMap<>();
            if(mangaMatcher.find()) {
                type = "manga";
                params.put("q", mangaMatcher.group(0).replace(" ", "%20"));
            }
            else if(animeMatcher.find()) {
                type = "anime";
                params.put("q", animeMatcher.group(0).replace(" ", "%20"));
            }
            else {
                return;
            }

            JSONObject response;
            try {
                response = HTTPUtil.getJSON(baseUrl + type, params);
            }
            catch (IOException e) {
                event.getChannel().sendMessage("Couldn't contact the Jikan API, try again later.").queue();
                e.printStackTrace();
                return;
            }

            MessageEmbed embed;
            if(type.equals("anime")) {
                JSONObject firstMatch = response.getJSONArray("results").getJSONObject(0);
                try {
                    embed = Anime.make(firstMatch).getGenericEmbed();
                }
                catch (ParseException e) {
                    event.getChannel().sendMessage("Something went wrong while parsing Jikan data.").queue();
                    e.printStackTrace();
                    return;
                }
            }
            else {
                JSONObject firstMatch = response.getJSONArray("results").getJSONObject(0);
                try {
                    embed = Manga.make(firstMatch).getGenericEmbed();
                }
                catch (ParseException e) {
                    event.getChannel().sendMessage("Something went wrong while parsing Jikan data.").queue();
                    e.printStackTrace();
                    return;
                }
            }

            event.getChannel().sendMessage(embed).queue();
        }
    }
}
