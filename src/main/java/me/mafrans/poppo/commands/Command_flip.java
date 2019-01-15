package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Command_flip implements ICommand {

    @Override
    public String getName() {
        return "flip";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Flips a coin!", "flip", new String[] {"coin"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        String heads = "https://i.imgur.com/pXfeqdK.png";
        String tails = "https://i.imgur.com/7oGSZMc.png";

        Random random = new Random();
        boolean flip = random.nextBoolean();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(0, 220, 255));
        embedBuilder.setFooter("Image: Google inc.", null);
        if(flip) {
            embedBuilder.addField("Coin Flip", "Heads!", false);
            embedBuilder.setImage(heads);
        }
        else {
            embedBuilder.addField("Coin Flip", "Tails!", false);
            embedBuilder.setImage(tails);
        }

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }
}
