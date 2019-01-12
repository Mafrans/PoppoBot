package me.mafrans.poppo.commands;

import me.mafrans.smiteforge.GameMode;
import me.mafrans.smiteforge.Player;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.SelectionList;
import me.mafrans.smiteforge.RankedTier;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Command_smite implements ICommand {
    @Override
    public String getName() {
        return "smite";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.WEB, "Gets information from the Smite API", "smite <name>", new ArrayList<>(), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length != 1) {
            return false;
        }

        int[] ids = Main.smiteForge.getPlayerIds(args[0]);
        if(ids.length == 0) {
            channel.sendMessage("Cannot find a Smite Player with that name.").queue();
            return true;
        }
        Map<Integer, Player> playerMap = new HashMap<>();
        for(int id : ids) {
            Player player;
            try {
                player = Main.smiteForge.getPlayer(id);
                playerMap.put(id, player);
            }
            catch (JSONException ignored) {
            }
        }
        SelectionList selectionList = new SelectionList("Select a Player", channel, command.getAuthor());

        for(int id : playerMap.keySet()) {
            Player player = playerMap.get(id);
            RankedTier tier = RankedTier.QUALIFYING;
            GameMode mode = GameMode.CONQUEST_RANKED;
            if(player.getConquestTier().toIndex() > tier.toIndex()) {
                tier = player.getConquestTier();
                mode = GameMode.CONQUEST_RANKED;
            }
            if(player.getDuelTier().toIndex() > tier.toIndex()) {
                tier = player.getDuelTier();
                mode = GameMode.DUEL_RANKED;
            }
            if(player.getJoustTier().toIndex() > tier.toIndex()) {
                tier = player.getJoustTier();
                mode = GameMode.JOUST_RANKED;
            }


            RankedTier finalTier = tier;
            GameMode finalMode = mode;
            selectionList.addAlternative(player.getName(), () -> {
                selectionList.getMessage().delete().complete();
                Message loadMessage = channel.sendMessage(":arrows_counterclockwise: Loading Profile...").complete();

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setThumbnail("attachment://rank.png");

                if(player.getTeam().getId() != 0) {
                    embedBuilder.addField("Team", player.getTeam().getName(), true);
                }

                embedBuilder.addField("Level", String.valueOf(player.getLevel()), true);
                embedBuilder.addField("Worshippers", String.valueOf(player.getWorshippers()), true);
                embedBuilder.addField("Conquest Rank", GUtil.capitalize(player.getConquestTier().getTier().toString()), true);
                embedBuilder.addField("Duel Rank", GUtil.capitalize(player.getDuelTier().getTier().toString()), true);
                embedBuilder.addField("Joust Rank", GUtil.capitalize(player.getJoustTier().getTier().toString()), true);
                embedBuilder.addField("Platform", player.getPlatform(), true);
                embedBuilder.addField("Mastery Level", String.valueOf(player.getMasteryLevel()), true);
                embedBuilder.addField("Account Created", GUtil.DATE_TIME_FORMAT.format(player.getDateCreated()), true);
                embedBuilder.addField("Last Login", GUtil.DATE_TIME_FORMAT.format(player.getLastLogin()), true);
                embedBuilder.addField("Wins/Losses", player.getGamesWon() + "/" + player.getGamesLost() + " - " + GUtil.round((float)player.getGamesWon()/(player.getGamesWon() + player.getGamesLost())*100, 1) + "%", true);
                embedBuilder.addField("Games Disconnected", String.valueOf(player.getGamesLeft()), true);

                String mostPlayedGod = "";
                int mostPlayedAmount = -1;
                int kills = 0;
                int deaths = 0;
                int assists = 0;

                for(GameMode gameMode : GameMode.values()) {
                    try {
                        JSONArray jsonArray = Main.smiteForge.getQueueStats(id, gameMode);
                        System.out.println(jsonArray);

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
                                mostPlayedGod = jsonObject.getString("God");
                            }
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                embedBuilder.addField("Kills/Deaths/Assists", kills + "/" + deaths + "/" + (assists/2) + " - " + GUtil.round(((float)kills+assists)/deaths, 1) + " KDA", true);
                embedBuilder.addField("Most Played God", mostPlayedGod, true);
                embedBuilder.setColor(GUtil.randomColor());
                InputStream file = null;
                if (player.getAvatarUrl().isEmpty()) {
                    embedBuilder.setAuthor(player.getName(), "http://smite.guru/profile/pc/" + player.getName().replaceFirst("\\[.+]", ""), null);
                }
                else {
                    try {
                        file = new URL(player.getAvatarUrl()).openStream();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    embedBuilder.setAuthor(player.getName(), "http://smite.guru/profile/pc/" + player.getName().replaceFirst("\\[.+]", ""), "attachment://avatar.png");
                }

                MessageBuilder messageBuilder = new MessageBuilder();
                messageBuilder.setEmbed(embedBuilder.build());

                InputStream rankedImage = null;
                switch(finalMode) {
                    case CONQUEST_RANKED:
                        rankedImage = GUtil.getSmiteConquestTierImage(finalTier);
                        break;
                    case DUEL_RANKED:
                        rankedImage = GUtil.getSmiteDuelTierImage(finalTier);
                        break;
                    case JOUST_RANKED:
                        rankedImage = GUtil.getSmiteJoustTierImage(finalTier);
                        break;
                }

                loadMessage.delete().complete();
                if (file != null) {
                    MessageAction action = channel.sendFile(file, "avatar.png", messageBuilder.build());
                    if(rankedImage != null) {
                        action.addFile(rankedImage, "rank.png");
                    }
                    action.queue();
                }
                else {
                    channel.sendMessage(embedBuilder.build()).queue();
                }
            });
        }

        selectionList.show(channel);
        return true;
    }
}
