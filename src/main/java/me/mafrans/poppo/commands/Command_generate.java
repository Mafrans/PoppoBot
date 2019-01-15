package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Command_generate implements ICommand {
    @Override
    public String getName() {
        return "generate";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Generates things.", "generate number|string|password|name [args...]", Collections.singletonList("make"), false, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();

        if(args.length < 1) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "number":
                return generateNumber(command, channel);

            case "string":
                return generateString(command, channel);

            case "password":
                return generatePassword(command, channel);

            case "name":
                return generateName(command, channel);
        }

        return false;
    }

    boolean generateNumber(Command command, TextChannel channel) {
        String[] args = command.getArgs();
        if(args.length < 3) {
            channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate number <min> <max>`").queue();
            return true;
        }

        float min;
        float max;
        try {
            min = Float.parseFloat(args[1]);
            max = Float.parseFloat(args[2]);
        }
        catch (NumberFormatException ex) {
            channel.sendMessage("Arguments <min> and <max> must be numbers!`").queue();
            channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate number <min> <max>`").queue();
            return true;
        }

        Random random = new Random();
        float randomFloat = random.nextFloat() * (max-min) + min;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        if(Math.round(min) == min && Math.round(max) == max) {
            embedBuilder.addField("Number Generator", String.valueOf(Math.round(randomFloat)), false);
        }
        else {
            embedBuilder.addField("Number Generator", String.valueOf(randomFloat), false);
        }

        embedBuilder.setThumbnail("https://i.imgur.com/St68rEk.png");
        embedBuilder.setColor(GUtil.randomColor());
        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }



    boolean generateString(Command command, TextChannel channel) {
        String[] args = command.getArgs();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz123456789";
        if(args.length < 2) {
            channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate string <length> [alphabet...]`").queue();
            return true;
        }
        if(args.length > 2) {
            alphabet = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ");
        }

        float length;
        try {
            length = Float.parseFloat(args[1]);
        }
        catch (NumberFormatException ex) {
            channel.sendMessage("Argument <length> must be a number!`").queue();
            channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate string <length> [alphabet...]`").queue();
            return true;
        }

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(randomNumber);

            stringBuilder.append(randomChar);
        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField("String Generator", stringBuilder.toString(), false);
        embedBuilder.setThumbnail("https://i.imgur.com/PecJAgE.png");
        embedBuilder.setColor(GUtil.randomColor());
        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }



    boolean generatePassword(Command command, TextChannel channel) throws IOException {
        String[] args = command.getArgs();

        if(args.length < 2) {
            channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate password <word-amount>`").queue();
            return true;
        }

        float amount;
        try {
            amount = Float.parseFloat(args[1]);
        }
        catch (NumberFormatException ex) {
            channel.sendMessage("Argument <word-amount> must be a number!`").queue();
            channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate password <word-amount>`").queue();
            return true;
        }

        URL url = new URL("https://www.eff.org/files/2016/09/08/eff_short_wordlist_1.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String[] dictionary = IOUtils.toString(reader).split("\\r?\\n");

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < amount; i++) {
            int randomNumber = random.nextInt(dictionary.length);
            String word = dictionary[randomNumber].substring(5);

            stringBuilder.append(word).append(" ");
        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail("https://i.imgur.com/wTU5i5e.png");
        embedBuilder.addField("Password Generator", stringBuilder.toString(), false);
        embedBuilder.setColor(GUtil.randomColor());
        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }



    boolean generateName(Command command, TextChannel channel) throws IOException {
        String[] args = command.getArgs();

        if(args.length < 1) {
            channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate name [name-amount] [lastname-amount]`").queue();
            return true;
        }

        float firstAmount = 1;
        float lastAmount = 1;

        if(args.length > 1) {
            try {
                firstAmount = Float.parseFloat(args[1]);
            }
            catch (NumberFormatException ex) {
                channel.sendMessage("Argument <name-amount> must be a number!`").queue();
                channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate name [name-amount] [lastname-amount]`").queue();
                return true;
            }
        }

        if(args.length > 2) {
            try {
                lastAmount = Float.parseFloat(args[2]);
            }
            catch (NumberFormatException ex) {
                channel.sendMessage("Argument <lastname-amount> must be a number!`").queue();
                channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `generate name [name-amount] [lastname-amount]`").queue();
                return true;
            }
        }

        URL url = new URL("https://raw.githubusercontent.com/dominictarr/random-name/master/first-names.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String[] firstDictionary = IOUtils.toString(reader).split("\\r?\\n");
        Random random = new Random();
        StringBuilder firstBuilder = new StringBuilder();
        for(int i = 0; i < firstAmount; i++) {
            int randomNumber = random.nextInt(firstDictionary.length);
            String word = firstDictionary[randomNumber];

            firstBuilder.append(word).append(" ");
        }



        URL url2 = new URL("https://raw.githubusercontent.com/dominictarr/random-name/master/names.txt");
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(url2.openStream()));
        String[] lastDictionary = IOUtils.toString(reader2).split("\\r?\\n");
        StringBuilder lastBuilder = new StringBuilder();
        for(int i = 0; i < lastAmount; i++) {
            int randomNumber = random.nextInt(lastDictionary.length);
            String word = lastDictionary[randomNumber];

            lastBuilder.append(word).append(" ");
        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField("Name Generator", firstBuilder.toString() + lastBuilder.toString(), false);
        embedBuilder.setThumbnail("https://i.imgur.com/iMJf4Ew.png");
        embedBuilder.setColor(GUtil.randomColor());
        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }
}
