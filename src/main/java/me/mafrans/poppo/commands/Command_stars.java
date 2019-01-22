package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

public class Command_stars implements ICommand {
    @Override
    public String getName() {
        return "stars";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Shows how many stars you have.", "stars", new String[] {"money", "cash", "star", "balance", "bal"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        int stars = 0;
        if(!Main.userList.getUsersFrom("uuid", command.getAuthor().getId()).isEmpty()) {
            stars = Main.userList.getUsersFrom("uuid", command.getAuthor().getId()).get(0).getStars();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();


        return true;
    }
}
