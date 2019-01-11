package me.mafrans.poppo.commands;

import me.mafrans.javadins.GameMode;
import me.mafrans.javadins.Player;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.SelectionList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Command_paladins implements ICommand {
    @Override
    public String getName() {
        return "paladins";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.WEB, "Gets information from the Paladins API", "paladins <name>", new ArrayList<>(), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length != 1) {
            return false;
        }

        int[] ids = Main.javadins.getPlayerIds(args[0]);
        if(ids.length == 0) {
            channel.sendMessage("Cannot find a Paladins Player with that name.").queue();
            return true;
        }
        Map<Integer, Player> playerMap = new HashMap<>();
        for(int id : ids) {
            Player player = Main.javadins.getPlayer(id);
            playerMap.put(id, player);
        }
        SelectionList selectionList = new SelectionList("Select a Player", channel, command.getAuthor());

        for(int id : playerMap.keySet()) {
            Player player = playerMap.get(id);
            selectionList.addAlternative(player.getName(), () -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setThumbnail(GUtil.getPaladinsTierImage(player.getRankedTier()));
                embedBuilder.addField("Level", player.getLevel() + " (" + player.getExperience() + "xp)", true);
                embedBuilder.addField("Platform", player.getPlatform(), true);
                embedBuilder.addField("Champions Owned", String.valueOf(player.getChampionsOwned()), true);
                embedBuilder.addField("Account Created", GUtil.DATE_TIME_FORMAT.format(player.getDateCreated()), true);
                embedBuilder.addField("Last Login", GUtil.DATE_TIME_FORMAT.format(player.getLastLogin()), true);
                embedBuilder.addField("Wins/Losses", player.getGamesWon() + "/" + player.getGamesLost() + " - " + GUtil.round((float)player.getGamesWon()/(player.getGamesWon() + player.getGamesLost())*100, 1) + "%", true);
                embedBuilder.addField("Games Disconnected", String.valueOf(player.getGamesLeft()), true);

                String mostPlayedChampion = "";
                int mostPlayedAmount = -1;
                int kills = 0;
                int deaths = 0;
                int assists = 0;

                for(GameMode gameMode : GameMode.values()) {
                    try {
                        JSONArray jsonArray = Main.javadins.getQueueStats(id, gameMode);

                        for(int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if(jsonObject.has("Kills")) {
                                kills += jsonObject.getInt("Kills");
                            }
                            if(jsonObject.has("Deaths")) {
                                deaths += jsonObject.getInt("Deaths");
                            }

                            if(jsonObject.has("Assists")) {
                                assists += jsonObject.getInt("Assists");
                            }

                            if(jsonObject.getInt("Matches") > mostPlayedAmount) {
                                mostPlayedAmount = jsonObject.getInt("Matches");
                                mostPlayedChampion = jsonObject.getString("Champion");
                            }
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                embedBuilder.addField("Kills/Deaths/Assists", kills + "/" + deaths + "/" + (assists/2) + " - " + GUtil.round(((float)kills+assists)/deaths, 1) + " KDA", true);
                embedBuilder.addField("Most Played Champion", mostPlayedChampion, true);
                embedBuilder.setColor(GUtil.randomColor());

                embedBuilder.setAuthor(player.getName(), "https://paladins.ninja/player/" + id, GUtil.getPaladinsChampionImage(mostPlayedChampion));
                selectionList.getMessage().delete().queue();
                channel.sendMessage(embedBuilder.build()).queue();
            });
        }

        selectionList.show(channel);
        return true;
    }
}
