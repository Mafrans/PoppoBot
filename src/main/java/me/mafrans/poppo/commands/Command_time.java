package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.SimpleDateFormat;
import java.util.Date;

@Id("commands::time")
public class Command_time implements ICommand {
    @Override
    public String getName() {
        return "time";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Shows the time", "time", new String[]{"now"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());

        Date date = new Date();
        embedBuilder.setDescription(new SimpleDateFormat("yy-MM-dd - hh:mm:ss").format(date));
        channel.sendMessage(embedBuilder.build()).queue();
        return true;
    }
}
