package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.config.ConfigEntry;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;

public class Command_move implements ICommand {
    @Override
    public String getName() {
        return "move";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta("Moves a message to the correct channel [Moderator Only]", "move <message-id> <channel>", Arrays.asList(),false, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        if(!command.getMessage().getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("No Permission!", ConfigEntry.HTTPD_URL.getString(), command.getAuthor().getAvatarUrl());
            embedBuilder.setDescription("You need the MESSAGE_MANAGE permission to use this command!");
            embedBuilder.setColor(new Color(175, 0, 0));
            return true;
        }

        String[] args = command.getArgs();
        Message message = channel.getMessageById(args[0]).complete();

        if(message == null) {
            return false;
        }

        if(command.getMessage().getMentionedChannels().isEmpty()) {
            return false;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setAuthor(message.getMember().getEffectiveName() + " said:", ConfigEntry.HTTPD_URL.getString(), message.getAuthor().getAvatarUrl());
        embedBuilder.setTimestamp(message.getCreationTime());
        embedBuilder.setDescription(message.getContent());
        MessageEmbed embed = embedBuilder.build();

        for(TextChannel textChannel : command.getMessage().getMentionedChannels()) {
            textChannel.sendMessage(embed).queue();
        }

        message.delete().queue();

        return true;
    }
}
