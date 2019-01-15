package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command_move implements ICommand {
    @Override
    public String getName() {
        return "move";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.MODERATION, "Moves a message to the correct channel", "move <message-id[,message-id-2...]> <channel>", Arrays.asList(),false, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        if(!command.doOverride() && !command.getMessage().getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
            embedBuilder.setDescription("You need the MESSAGE_MANAGE permission to use this command!");
            embedBuilder.setColor(new Color(175, 0, 0));
            channel.sendMessage(embedBuilder.build()).queue();
            return true;
        }

        String[] args = command.getArgs();
        List<Message> messageList = new ArrayList<>();
        for(String id : args[0].split(",")) {
            Message message = channel.getMessageById(id).complete();
            if(message != null) {
                messageList.add(message);
            }
        }

        if(messageList.size() == 0) {
            return false;
        }

        if(command.getMessage().getMentionedChannels().isEmpty()) {
            return false;
        }

        for(Message message : messageList) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(GUtil.randomColor());
            embedBuilder.setAuthor(message.getMember().getEffectiveName() + " said:", Main.config.httpd_url, message.getAuthor().getAvatarUrl());
            embedBuilder.setTimestamp(message.getCreationTime());
            embedBuilder.setDescription(message.getContentRaw());
            MessageEmbed embed = embedBuilder.build();

            for (TextChannel textChannel : command.getMessage().getMentionedChannels()) {
                textChannel.sendMessage(embed).queue();
            }

            message.delete().queue();
        }

        return true;
    }
}
