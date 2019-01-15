package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                Anime anime = new Anime();
                anime.setAiring(animeObject.getBoolean("airing"));
                anime.setEpisodes(animeObject.getInt("episodes"));
                anime.setScore(animeObject.getDouble("score"));
                anime.setTitle(animeObject.getString("title"));
                anime.setSynopsis(animeObject.getString("synopsis"));
                anime.setImageUrl(animeObject.getString("image_url"));
                anime.setUrl(animeObject.getString("url"));
                anime.setType(animeObject.getString("type"));
                anime.setMembers(animeObject.getInt("members"));
                anime.setRating(null);
                if(animeObject.get("rated") instanceof String) {
                    anime.setRating(animeObject.getString("rated"));
                }
                anime.setMalId(animeObject.getInt("mal_id"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                anime.setStartDate(null);
                anime.setEndDate(null);
                if(animeObject.get("start_date") != null && animeObject.get("start_date") instanceof String) {
                    Date startDate = dateFormat.parse(animeObject.getString("start_date").substring(0, 10));
                    anime.setStartDate(startDate);
                }
                if(animeObject.get("end_date") != null && animeObject.get("end_date") instanceof String) {
                    Date endDate = dateFormat.parse(animeObject.getString("end_date").substring(0, 10));
                    anime.setEndDate(endDate);
                }


                selectionList.addAlternative(anime.getTitle() + "(" + anime.getType() + ")" + (anime.getRating() != null ? " [" + anime.getRating() + "]" : ""), () -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor(anime.getTitle() + "(" + anime.getType() + ")" + (anime.getRating() != null ? " [" + anime.getRating() + "]" : ""), anime.getUrl(), "https://cdn.myanimelist.net/img/sp/icon/apple-touch-icon-256.png");
                    embedBuilder.setThumbnail(anime.getImageUrl());
                    embedBuilder.addField("Synopsis", anime.getSynopsis(), false);
                    embedBuilder.addField("Airing", anime.isAiring() ? "Yes" : "No", true);
                    embedBuilder.addField("Episodes", String.valueOf(anime.getEpisodes()), true);
                    if(anime.getStartDate() != null) {
                        embedBuilder.addField("First Aired", GUtil.DATE_TIME_FORMAT.format(anime.getStartDate()).substring(0, 10), true);
                    }
                    if(anime.getEndDate() != null) {
                        embedBuilder.addField("Stopped Airing", GUtil.DATE_TIME_FORMAT.format(anime.getEndDate()).substring(0, 10), true);
                    }
                    embedBuilder.addField("Score", anime.getScore() + " / 10", true);
                    embedBuilder.addField("Members", String.valueOf(anime.getMembers()), true);
                    embedBuilder.setColor(GUtil.randomColor());

                    selectionList.getMessage().delete().queue();
                    channel.sendMessage(embedBuilder.build()).queue();
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
                Manga manga = new Manga();
                manga.setChapters(mangaObject.getInt("chapters"));
                manga.setVolumes(mangaObject.getInt("volumes"));
                manga.setPublished(mangaObject.getBoolean("publishing"));
                manga.setScore(mangaObject.getDouble("score"));
                manga.setTitle(mangaObject.getString("title"));
                manga.setSynopsis(mangaObject.getString("synopsis"));
                manga.setImageUrl(mangaObject.getString("image_url"));
                manga.setUrl(mangaObject.getString("url"));
                manga.setType(mangaObject.getString("type"));
                manga.setMembers(mangaObject.getInt("members"));
                manga.setMalId(mangaObject.getInt("mal_id"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                manga.setStartDate(null);
                manga.setEndDate(null);
                if(mangaObject.get("start_date") != null && mangaObject.get("start_date") instanceof String) {
                    Date startDate = dateFormat.parse(mangaObject.getString("start_date").substring(0, 10));
                    manga.setStartDate(startDate);
                }
                if(mangaObject.get("end_date") != null && mangaObject.get("end_date") instanceof String) {
                    Date endDate = dateFormat.parse(mangaObject.getString("end_date").substring(0, 10));
                    manga.setEndDate(endDate);
                }


                selectionList.addAlternative(manga.getTitle() + "(" + manga.getType() + ")", () -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor(manga.getTitle() + "(" + manga.getType() + ")", manga.getUrl(), "https://cdn.myanimelist.net/img/sp/icon/apple-touch-icon-256.png");
                    embedBuilder.setThumbnail(manga.getImageUrl());
                    embedBuilder.addField("Synopsis", manga.getSynopsis(), false);
                    embedBuilder.addField("Professionally Published", manga.isPublished() ? "Yes" : "No", true);

                    if(manga.getChapters() > 0 || manga.getVolumes() > 0) {
                        if(manga.getVolumes() > 0) {
                            embedBuilder.addField("Chapters", manga.getVolumes() + " Volumes " + manga.getChapters() + " Chapters", true);
                        }
                        else {
                            embedBuilder.addField("Chapters", manga.getChapters() + " Chapters", true);
                        }
                    }


                    if(manga.getStartDate() != null) {
                        embedBuilder.addField("First Released", GUtil.DATE_TIME_FORMAT.format(manga.getStartDate()).substring(0, 10), true);
                    }
                    if(manga.getEndDate() != null) {
                        embedBuilder.addField("Completed", GUtil.DATE_TIME_FORMAT.format(manga.getEndDate()).substring(0, 10), true);
                    }
                    embedBuilder.addField("Score", manga.getScore() + " / 10", true);
                    embedBuilder.addField("Members", String.valueOf(manga.getMembers()), true);
                    embedBuilder.setColor(GUtil.randomColor());

                    selectionList.getMessage().delete().queue();
                    channel.sendMessage(embedBuilder.build()).queue();
                });
            }

            selectionList.show(channel);
        }

        return true;
    }
}
