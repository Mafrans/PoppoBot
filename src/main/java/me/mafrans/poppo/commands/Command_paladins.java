package me.mafrans.poppo.commands;

import me.mafrans.javadins.*;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.SelectionList;
import me.mafrans.poppo.util.images.ImageBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class Command_paladins implements ICommand {
    @Override
    public String getName() {
        return "paladins";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.WEB, "Gets information from the Paladins API", "paladins player|match|latest <name|id>", new ArrayList<>(), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("player")) {
            if(args.length != 2) return false;

            int[] ids = Main.javadins.getPlayerIds(args[1]);
            if (ids.length == 0) {
                channel.sendMessage("Cannot find a Paladins Player with that name.").queue();
                return true;
            }
            Map<Integer, Player> playerMap = new HashMap<>();
            for (int id : ids) {
                Player player = Main.javadins.getPlayer(id);
                playerMap.put(id, player);
            }
            SelectionList selectionList = new SelectionList("Select a Player", channel, command.getAuthor());

            for (int id : playerMap.keySet()) {
                Player player = playerMap.get(id);
                selectionList.addAlternative(player.getName(), () -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setThumbnail("attachment://rank.png");
                    embedBuilder.addField("Level", player.getLevel() + " (" + player.getExperience() + "xp)", true);
                    embedBuilder.addField("Platform", player.getPlatform(), true);
                    embedBuilder.addField("Champions Owned", String.valueOf(player.getChampionsOwned()), true);
                    embedBuilder.addField("Account Created", GUtil.DATE_TIME_FORMAT.format(player.getDateCreated()), true);
                    embedBuilder.addField("Last Login", GUtil.DATE_TIME_FORMAT.format(player.getLastLogin()), true);
                    embedBuilder.addField("Wins/Losses", player.getGamesWon() + "/" + player.getGamesLost() + " - " + GUtil.round((float) player.getGamesWon() / (player.getGamesWon() + player.getGamesLost()) * 100, 1) + "%", true);
                    embedBuilder.addField("Games Disconnected", String.valueOf(player.getGamesLeft()), true);

                    String mostPlayedChampion = "";
                    int mostPlayedAmount = -1;
                    int kills = 0;
                    int deaths = 0;
                    int assists = 0;

                    for (GameMode gameMode : GameMode.values()) {
                        try {
                            JSONArray jsonArray = Main.javadins.getQueueStats(id, gameMode);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                if (jsonObject.has("Kills")) {
                                    kills += jsonObject.getInt("Kills");
                                }
                                if (jsonObject.has("Deaths")) {
                                    deaths += jsonObject.getInt("Deaths");
                                }

                                if (jsonObject.has("Assists")) {
                                    assists += jsonObject.getInt("Assists");
                                }

                                if (jsonObject.getInt("Matches") > mostPlayedAmount) {
                                    mostPlayedAmount = jsonObject.getInt("Matches");
                                    mostPlayedChampion = jsonObject.getString("Champion");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    embedBuilder.addField("Kills/Deaths/Assists", kills + "/" + deaths + "/" + (assists / 2) + " - " + GUtil.round(((float) kills + assists) / deaths, 1) + " KDA", true);
                    embedBuilder.addField("Most Played Champion", mostPlayedChampion, true);
                    embedBuilder.setColor(GUtil.randomColor());

                    embedBuilder.setAuthor(player.getName(), "https://paladins.ninja/player/" + id, GUtil.getPaladinsChampionImage(mostPlayedChampion));
                    selectionList.getMessage().delete().queue();

                    Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
                    if(GUtil.getPaladinsTierImage(player.getRankedTier()) != null) {
                        channel.sendFile(GUtil.getPaladinsTierImage(player.getRankedTier()), "rank.png", message).queue();
                    }
                    else {
                        channel.sendMessage(message).queue();
                    }
                });
            }

            selectionList.show(channel);
            return true;
        }

        if(args[0].equalsIgnoreCase("match")) {
            if(args.length != 2) return false;

            if(args[1].length() != 9 || !NumberUtils.isDigits(args[1])) {
                channel.sendMessage("Invalid Match ID, the Match ID should be a 9 digit long number.\n" +
                        "Are you looking for the latest game of a player? Try `hey poppo paladins latest <name>`").queue();
                return true;
            }

            Match match = Main.javadins.getMatch(Integer.parseInt(args[1]));
            if(match == null) {
                channel.sendMessage("Could not find that Paladins Match, are you sure that the ID is correct?").queue();
                return true;
            }

            Message loadingMessage2 = channel.sendMessage(":arrows_counterclockwise: Loading Match Info...").complete();
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(ImageBuilder.toBufferedImage(generateImage(match)),"png", os);
                InputStream fis = new ByteArrayInputStream(os.toByteArray());
                loadingMessage2.delete().complete();
                channel.sendFile(fis, "image.png").queue();
            }
            catch (IOException | FontFormatException e) {
                e.printStackTrace();
                loadingMessage2.delete().complete();
                channel.sendMessage("Something happened while loading your match, please try again.").queue();
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("latest")) {
            if(args.length != 2) return false;

            int[] ids = Main.javadins.getPlayerIds(args[1]);
            if (ids.length == 0) {
                channel.sendMessage("Cannot find a Paladins Player with that name.").queue();
                return true;
            }
            Map<Integer, Player> playerMap = new HashMap<>();
            for (int id : ids) {
                Player player = Main.javadins.getPlayer(id);
                playerMap.put(id, player);
            }
            SelectionList playerSelectionList = new SelectionList("Select a Player", channel, command.getAuthor());

            for (int id : playerMap.keySet()) {
                Player player = playerMap.get(id);
                playerSelectionList.addAlternative(player.getName(), () -> {
                    playerSelectionList.getMessage().delete().complete();
                    Message loadingMessage = channel.sendMessage(":arrows_counterclockwise: Loading Matches...").complete();

                    SelectionList matchSelectionList = new SelectionList("Select a Match", channel, command.getAuthor());
                    try {
                        for(Match match : player.getMatchHistory(10)) {
                            MatchPlayer matchPlayer = null;
                            for(MatchPlayer mp : match.getPlayers()) {
                                if(mp.getId() == player.getId()) {
                                    matchPlayer = mp;
                                    break;
                                }
                            }

                            matchSelectionList.addAlternative(match.getGameMode().getName() + ": " + match.getMap() + (matchPlayer == null ? "" : " (" + matchPlayer.getChampion() + ")"), () -> {
                                Message loadingMessage2 = channel.sendMessage(":arrows_counterclockwise: Loading Match Info...").complete();
                                matchSelectionList.getMessage().delete().queue();
                                try {
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    ImageIO.write(ImageBuilder.toBufferedImage(generateImage(match)),"png", os);
                                    InputStream fis = new ByteArrayInputStream(os.toByteArray());
                                    loadingMessage2.delete().complete();
                                    channel.sendFile(fis, "image.png").queue();
                                }
                                catch (IOException | FontFormatException | ParseException e) {
                                    e.printStackTrace();
                                    loadingMessage2.delete().complete();
                                    channel.sendMessage("Something happened while loading your match, please try again.").queue();
                                }
                            });
                        }
                        loadingMessage.delete().complete();
                        matchSelectionList.show(channel);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            playerSelectionList.show(channel);
            return true;
        }

        return false;
    }

    public static Image generateImage(Match match) throws IOException, FontFormatException, ParseException {

        final Font lato = GUtil.getTrueTypeFont("fonts/Lato-Regular.ttf");

        String mapName = match.getMap().replaceFirst("Ranked ", "");
        System.out.println(mapName);

        ImageBuilder imageBuilder = new ImageBuilder(817, 1000);
        if(ClassLoader.getSystemResourceAsStream("images/maps/" + mapName + ".jpg") != null) {
            imageBuilder.addBackground(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/maps/" + mapName + ".jpg")), ImageBuilder.FitType.CENTER, false, false, 20);
        }
        else {
            imageBuilder.addBackground(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/maps/Timber Mill.jpg")), ImageBuilder.FitType.CENTER, false, false, 20);
        }
        imageBuilder.setColor(new Color(0, 0, 0, 110)).addShape(new Rectangle(0, 0, 817, 1000), true);


        // Header
        Shape shape = new Rectangle(0, 0, 817, 84);
        imageBuilder.setColor(new Color(0, 0, 0, 135)).addShape(shape, true);

        int[] duration = GUtil.parseToMinutesSeconds(match.getDuration());

        String minutes = duration[0] < 10 ? "0" + duration[0] : String.valueOf(duration[0]);
        String seconds = duration[1] < 10 ? "0" + duration[1] : String.valueOf(duration[1]);

        imageBuilder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imageBuilder.setColor(Color.WHITE).setTextFont(lato).addText(match.getGameMode().getName() + " • " + mapName + " • " + minutes + ":" + seconds, 30, 50, 0, 25);

        imageBuilder.addImage(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/Paladins_Icon.png")), 687, 20, 100, 44, Image.SCALE_SMOOTH);



        // Winning Team

        imageBuilder.setColor(new Color(76, 222, 239)).addText("Team 1", 86, 160, Font.BOLD, 30);
        imageBuilder.addShape(new Rectangle(196, 124, 46, 48), true);

        int winningScore = Math.max(match.getScore()[0], match.getScore()[1]);
        imageBuilder.setColor(Color.WHITE).addText(String.valueOf(winningScore), 210, 160, Font.BOLD, 30);

        imageBuilder.setColor(Color.WHITE);
        imageBuilder.addText("K / D / A", 268, 170, Font.BOLD, 23);
        imageBuilder.addText("Damage", 381, 170, Font.BOLD, 23);
        imageBuilder.addText("Taken", 492, 170, Font.BOLD, 23);
        imageBuilder.addText("Healing", 584, 170, Font.BOLD, 23);
        imageBuilder.addText("Shielding", 675, 170, Font.BOLD, 23);


        int margin = 15 + 50;
        int i = 0;
        int averageWinTier = 0;
        for(MatchPlayer player : match.getPlayers()) {
            if(player.isWinner()) {
                averageWinTier += Main.javadins.getPlayer(Main.javadins.getPlayerIds(player.getName())[0]).getRankedTier().toIndex();

                imageBuilder.setTextFont(new Font(lato.getFamily(), 0, 23));
                FontMetrics fontMetrics = imageBuilder.getGraphics().getFontMetrics();

                int textWidth = fontMetrics.stringWidth(String.format("%s / %s / %s", player.getKills(), player.getDeaths(), player.getAssists()));
                imageBuilder.setColor(Color.WHITE).addText(String.format("%s / %s / %s", player.getKills(), player.getDeaths(), player.getAssists()), 312 - textWidth/2, 235 + margin*i, 0, 23);

                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                textWidth = fontMetrics.stringWidth(nf.format(player.getDamage()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getDamage()), 422 - textWidth/2, 235 + margin*i, 0, 23);

                textWidth = fontMetrics.stringWidth(nf.format(player.getTaken()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getTaken()), 522 - textWidth/2, 235 + margin*i, 0, 23);

                textWidth = fontMetrics.stringWidth(nf.format(player.getHealing()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getHealing()), 622 - textWidth/2, 235 + margin*i, 0, 23);

                textWidth = fontMetrics.stringWidth(nf.format(player.getShielding()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getShielding()), 722 - textWidth/2, 235 + margin*i, 0, 23);


                // Name and picture
                imageBuilder.setColor(new Color(76, 222, 239)).addShape(new Rectangle(30, 202 + margin*i, 50, 50), true);
                imageBuilder.addImage(ImageIO.read(new URL(GUtil.getPaladinsChampionImage(player.getChampion()))), 32, 204 + margin*i, 46, 46, Image.SCALE_SMOOTH);

                imageBuilder.addText(player.getName(), 95, 222 + margin*i, Font.BOLD, 23);
                imageBuilder.setColor(new Color(255, 255, 255, 179)).addText(player.getChampion(), 95, 248 + margin*i, 0, 23);


                i++;
            }
        }
        averageWinTier /= 5;
        imageBuilder.addImage(ImageIO.read(GUtil.getPaladinsTierImage(RankedTier.fromIndex(averageWinTier))), 28, 126, 55, 55, Image.SCALE_SMOOTH);



        // Losing Team
        int topMargin = 438;

        imageBuilder.setColor(new Color(231, 71, 76)).addText("Team 2", 86, 160 + topMargin, Font.BOLD, 30);
        imageBuilder.addShape(new Rectangle(196, 124 + topMargin, 46, 48), true);
        imageBuilder.setColor(Color.WHITE).addText(String.valueOf(4 - winningScore), 210, 160 + topMargin, Font.BOLD, 30);

        imageBuilder.setColor(Color.WHITE);
        imageBuilder.addText("K / D / A", 268, 170 + topMargin, Font.BOLD, 23);
        imageBuilder.addText("Damage", 381, 170 + topMargin, Font.BOLD, 23);
        imageBuilder.addText("Taken", 492, 170 + topMargin, Font.BOLD, 23);
        imageBuilder.addText("Healing", 584, 170 + topMargin, Font.BOLD, 23);
        imageBuilder.addText("Shielding", 675, 170 + topMargin, Font.BOLD, 23);


        i = 0;
        int averageLoseTier = 0;
        for(MatchPlayer player : match.getPlayers()) {
            if(!player.isWinner()) {
                averageLoseTier += Main.javadins.getPlayer(Main.javadins.getPlayerIds(player.getName())[0]).getRankedTier().toIndex();
                imageBuilder.setTextFont(new Font(lato.getFamily(), 0, 23));
                FontMetrics fontMetrics = imageBuilder.getGraphics().getFontMetrics();

                int textWidth = fontMetrics.stringWidth(String.format("%s / %s / %s", player.getKills(), player.getDeaths(), player.getAssists()));
                imageBuilder.setColor(Color.WHITE).addText(String.format("%s / %s / %s", player.getKills(), player.getDeaths(), player.getAssists()), 312 - textWidth/2, 235 + topMargin + margin*i, 0, 23);

                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                textWidth = fontMetrics.stringWidth(nf.format(player.getDamage()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getDamage()), 422 - textWidth/2, 235 + topMargin + margin*i, 0, 23);

                textWidth = fontMetrics.stringWidth(nf.format(player.getTaken()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getTaken()), 522 - textWidth/2, 235 + topMargin + margin*i, 0, 23);

                textWidth = fontMetrics.stringWidth(nf.format(player.getHealing()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getHealing()), 622 - textWidth/2, 235 + topMargin + margin*i, 0, 23);

                textWidth = fontMetrics.stringWidth(nf.format(player.getShielding()));
                imageBuilder.setColor(Color.WHITE).addText(nf.format(player.getShielding()), 722 - textWidth/2, 235 + topMargin + margin*i, 0, 23);


                // Name and picture
                imageBuilder.setColor(new Color(231, 71, 76)).addShape(new Rectangle(30, 202 + topMargin + margin*i, 50, 50), true);
                imageBuilder.addImage(ImageIO.read(new URL(GUtil.getPaladinsChampionImage(player.getChampion()))), 32, 204 + topMargin + margin*i, 46, 46, Image.SCALE_SMOOTH);

                imageBuilder.addText(player.getName(), 95, 222 + topMargin + margin*i, Font.BOLD, 23);
                imageBuilder.setColor(new Color(255, 255, 255, 179)).addText(player.getChampion(), 95, 248 + topMargin + margin*i, 0, 23);

                i++;
            }

        }
        averageLoseTier /= 5;
        imageBuilder.addImage(ImageIO.read(GUtil.getPaladinsTierImage(RankedTier.fromIndex(averageLoseTier))), 28, 126 + topMargin, 55, 55, Image.SCALE_SMOOTH);

        return imageBuilder.build();
    }


}
