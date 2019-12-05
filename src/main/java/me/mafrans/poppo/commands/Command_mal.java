package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.SelectionList;
import me.mafrans.poppo.util.objects.Anime;
import me.mafrans.poppo.util.objects.Manga;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Id("commands::myanimelist")
public class Command_mal implements ICommand {
    @Override
    public String getName() {
        return "mal";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.WEB, "Searches MyAnimeList for shows or mangas", "mal [anime|manga] <name>", new String[] {"myanimelist"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();

        String baseUrl = "https://api.jikan.moe/v3/search/";
        String type = "anime";
        boolean ignoreFirstArg = false;

        if(args.length < 1) {
            return false;
        }

        if(args[0].equalsIgnoreCase("manga")) {
            type = "manga";
            ignoreFirstArg = true;
        }
        if(args[0].equalsIgnoreCase("anime")) {
            ignoreFirstArg = true;
        }

        String query;
        if(ignoreFirstArg) {
            if(args.length < 2) {
                return false;
            }
            query = StringUtils.join(ArrayUtils.subarray(args, 1, args.length));
        }
        else {
            query = StringUtils.join(args);
        }

        Map<String, String> params = new HashMap<>();
        params.put("q",  query.replace(" ", "%20"));

        JSONObject response = HTTPUtil.getJSON(baseUrl + type, params);
        SelectionList selectionList = new SelectionList("Select a result", channel, command.getAuthor());

        if(type.equals("anime")) {
            if(response.getJSONArray("results").length() == 0) {
                channel.sendMessage("I found no results for that search query.").queue();
                return true;
            }
            for (int i = 0; i < Math.min(10, response.getJSONArray("results").length()); i++) {
                JSONObject animeObject = response.getJSONArray("results").getJSONObject(i);
                Anime anime = Anime.make(animeObject);


                selectionList.addAlternative(anime.getTitle() + "(" + anime.getType() + ")" + (anime.getRating() != null ? " [" + anime.getRating() + "]" : ""), () -> {
                    selectionList.getMessage().delete().queue();
                    channel.sendMessage(anime.getGenericEmbed()).queue();
                });
            }

            selectionList.show(channel);
        }

        if(type.equals("manga")) {
            if(response.getJSONArray("results").length() == 0) {
                channel.sendMessage("I found no results for that search query.").queue();
                return true;
            }
            for (int i = 0; i < Math.min(10, response.getJSONArray("results").length()); i++) {
                JSONObject mangaObject = response.getJSONArray("results").getJSONObject(i);
                Manga manga = Manga.make(mangaObject);


                selectionList.addAlternative(manga.getTitle() + "(" + manga.getType() + ")", () -> {
                    selectionList.getMessage().delete().queue();
                    channel.sendMessage(manga.getGenericEmbed()).queue();
                });
            }

            selectionList.show(channel);
        }

        return true;
    }
}
