package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

@Id("commands::gamble")
public class Command_gamble implements ICommand {
    private Map<String, Message> gambleMessages = new HashMap<>();
    @Override
    public String getName() {
        return "gamble";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Gamble your worries away.", "gamble <amount>", new String[] {"slots"}, false);
    }

    private String[] emojis = new String[] {
            "grapes",
            "grapes", // 0.057
            "grapes",

            "lemon",
            "lemon", // 0.057
            "lemon",

            "eggplant", // 0.021
            "eggplant",

            "cherries", // 0.021
            "cherries",

            "thinking", // 0.002

            "seven", // 0.002
    };

    private static Map<String, Object[]> results = new HashMap<>();
    static {
        results.put("grapes", new Object[] {2f, "It's a win! You won %d stars."});
        results.put("lemon", new Object[] {3f, "It's a win! You won %d stars."});
        results.put("eggplant", new Object[] {5f, "Big win!\n You won 5 times as many stars for a total of %d!"});
        results.put("cherries", new Object[] {10f, "Big win!\n You won 10 times your bet for a total of %d!"});
        results.put("thinking", new Object[] {20f, "Huge win!\n You won 20 times as many stars for a total of %d!"});
        results.put("seven", new Object[] {100f, ":tada: **JACKPOT!** :tada:\nYou won 100 times as many stars for %d stars!"});
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length < 1) {
            return false;
        }

        int bettedStars;

        if(gambleMessages.containsKey(command.getAuthor().getId())) {
            channel.sendMessage("You cannot gamble twice at the same time.").queue();
            return true;
        }

        if(args.length > 1 && (StringUtils.join(args, " ").toLowerCase().startsWith("all in") || StringUtils.join(args, " ").toLowerCase().startsWith("all-in") || StringUtils.join(args, " ").toLowerCase().startsWith("allin"))) {
            bettedStars = Main.userList.getUsersFrom("uuid", command.getAuthor().getId()).get(0).getStars();
            channel.sendMessage("All in, baby!").queue();
        }
        else if(!NumberUtils.isDigits(args[0])) {
            return false;
        }
        else {
            bettedStars = Integer.parseInt(args[0]);
        }

        if(bettedStars < 1) {
            channel.sendMessage("Silly, you can't bet less than one star.").queue();
            return true;
        }

        if(Main.userList.getUsersFrom("uuid", command.getAuthor().getId()).get(0).getStars() < bettedStars) {
            channel.sendMessage("You don't have enough stars to do that, silly.").queue();
            return true;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Betting " + bettedStars + " stars.", null, command.getAuthor().getEffectiveAvatarUrl());
        embedBuilder.setDescription(
                "╔══════════╗\n" +
                "║ :question: | :question: | :question:    ║\n" +
                "╚══════════╝");
        embedBuilder.setColor(GUtil.randomColor());

        Message message = channel.sendMessage(embedBuilder.build()).complete();
        gambleMessages.put(command.getAuthor().getId(), message);


        int finalBettedStars = bettedStars;
        Thread thread = new Thread(() -> {
            Message msg = gambleMessages.get(command.getAuthor().getId());
            EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0));
            Random random = new Random();
            List<String> list = new ArrayList<>();
            Collections.addAll(list, emojis);
            int duplicates;

            String emote1 = list.get(random.nextInt(list.size()));
            duplicates = GUtil.getDuplicatesOf(list, emote1);
            for(int i = 0; i < duplicates; i++) {
                list.add(emote1);
                System.out.println(emote1);
            }

            String emote2 = list.get(random.nextInt(list.size()));
            duplicates = GUtil.getDuplicatesOf(list, emote1);
            for(int i = 0; i < duplicates; i++) {
                list.add(emote2);
                System.out.println(emote1);
            }

            String emote3 = list.get(random.nextInt(list.size()));

            System.out.println("0");
            try {

                Thread.sleep(1500);
                System.out.println("1");
                eb.setDescription(
                        "╔══════════╗\n" +
                        "║ :" + emote1 + ": | :question: | :question:    ║\n" +
                        "╚══════════╝");
                msg.editMessage(eb.build()).complete();

                Thread.sleep(1000);
                System.out.println("2");
                eb.setDescription(
                        "╔══════════╗\n" +
                        "║ :" + emote1 + ": | :" + emote2 + ": | :question:    ║\n" +
                        "╚══════════╝");
                msg.editMessage(eb.build()).complete();

                Thread.sleep(1000);
                System.out.println("3");
                eb.setDescription(
                        "╔══════════╗\n" +
                        "║ :" + emote1 + ": | :" + emote2 + ": | :" + emote3 + ":    ║\n" +
                        "╚══════════╝");
                msg.editMessage(eb.build()).complete();
                Thread.sleep(500);
                System.out.println("4");
                String result = "";

                int wonStars = 0;
                if(emote1.equals(emote2) && emote1.equals(emote3)) {
                    wonStars = (int) Math.floor(finalBettedStars * (float)results.get(emote1)[0]);
                    result = (String) results.get(emote1)[1];
                }

                DataUser dataUser = Main.userList.getUsersFrom("uuid", command.getAuthor().getId()).get(0);
                int oldStars = dataUser.getStars();
                int newStars = oldStars - finalBettedStars + wonStars;
                dataUser.setStars(newStars);
                Main.userList.put(new SQLDataUser(dataUser));

                if(wonStars > 0) {
                    eb.addField("Result", String.format(result + "\nYou now have " + newStars + " stars.", wonStars), false);
                }
                else {
                    eb.addField("Result", "Aww, better luck next time!\nYou now have " + newStars + " stars.", false);
                }
                msg.editMessage(eb.build()).complete();
                gambleMessages.remove(command.getAuthor().getId());
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        thread.start();

        return true;
    }
}
