package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import net.dv8tion.jda.client.managers.EmoteManager;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Command_roll implements ICommand {
    @Override
    public String getName() {
        return "roll";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                "Rolls any amount of dice and sums the values.",
                "roll [amount] [type]",
                null,
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        String[] args = command.getArgs();
        int amount = 1;
        int sides = 6;
        int maxAmount = 100;

        if(args.length == 0) {
            channel.sendMessage(":game_die: Rolling a D" + sides).complete();
            Thread.sleep(500);
            channel.sendMessage("You rolled a " + getNumberEmote((int)rollDie(6)) + "").queue();
            return true;
        }

        if(args.length == 1) {
            if(!NumberUtils.isParsable(args[0])) {
                return false;
            }
            amount = Integer.parseInt(args[0]);
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

            channel.sendMessage(":game_die: Rolling " + amount + " D" + sides + " dice.").complete();
            Thread.sleep(1000);
            channel.sendMessage("You rolled:").complete();

            int count = 0;
            StringBuilder diceBuilder = new StringBuilder();
            for(int d : rolledDice) {
                if(count == 0) {
                    diceBuilder.append(getNumberEmote(d));
                }
                else {
                    diceBuilder.append(" " + getNumberEmote(d));
                }

                count++;
                if((amount >= 20 && count == 20) || (count == amount)) {
                    diceBuilder.append("\u2063");
                    channel.sendMessage(diceBuilder.toString()).queue();
                    diceBuilder = new StringBuilder();
                    amount -= count;
                    count = 0;
                }
            }

            channel.sendMessage("The sum of all your dice rolls is **" + total + "**").queue();
            return true;
        }

        return false;
    }

    public int rollDie(int sides) {
        Random random = new Random();
        return random.nextInt(sides) + 1;
    }

    public String getNumberEmote(int number) {
        switch(number) {
            case 0:
                return ":zero:";
            case 1:
                return ":one:";
            case 2:
                return ":two:";
            case 3:
                return ":three:";
            case 4:
                return ":four:";
            case 5:
                return ":five:";
            case 6:
                return ":six:";
            case 7:
                return ":seven:";
            case 8:
                return ":eight:";
            case 9:
                return ":nine:";
            case 10:
                return ":keycap_ten:";
        }
        return ":asterisk:";
    }
}
