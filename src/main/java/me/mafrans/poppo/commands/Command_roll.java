package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Id("commands::roll")
public class Command_roll implements ICommand {
    @Override
    public String getName() {
        return "roll";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.FUN,
                "Rolls any amount of dice and sums the values.",
                "roll [amount]",
                null,
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        String[] args = command.getArgs();
        int amount;
        int sides = 6;
        int maxAmount = 100;

        if(args.length == 0) {
            channel.sendMessage(":game_die: Rolling a D" + sides).complete();
            Thread.sleep(500);
            channel.sendMessage("You rolled a " + GUtil.getNumberEmote(rollDie(6)) + "").queue();
            return true;
        }

        if(args.length == 1) {
            if(!NumberUtils.isParsable(args[0])) {
                return false;
            }

            amount = (int) Math.round(Double.parseDouble(args[0]));
            if(amount > maxAmount) {
                channel.sendMessage("I'm sorry, but I only have " + maxAmount + " dice in my backpack.").queue();
                return true;
            }

            int total = 0;
            List<Integer> rolledDice = new ArrayList<>();
            for(int i = 0; i < amount; i++) {
                int rolledNumber = rollDie(sides);
                rolledDice.add(rolledNumber);
                total += rolledNumber;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(command.getMessage().getMember().getEffectiveName() + " rolled:", "https://google.com", command.getAuthor().getAvatarUrl());

            StringBuilder diceBuilder = new StringBuilder();
            for(int d : rolledDice) {
                diceBuilder.append(GUtil.getNumberEmote(d));
            }

            Random random = new Random();
            embedBuilder.setDescription(diceBuilder.toString());
            embedBuilder.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            embedBuilder.addField("Result",     "The sum of all your dice rolls is **" + total + "**\n" +
                                                            "The average of all your dice rolls is **" + total/(float)amount + "**", false);

            channel.sendMessage(embedBuilder.build()).queue();
            return true;
        }

        return false;
    }

    private int rollDie(int sides) {
        Random random = new Random();
        return random.nextInt(sides) + 1;
    }
}
