package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.beans.Expression;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Id("commands::uwu")
public class Command_uwu implements ICommand {
    @Override
    public String getName() {
        return "uwu";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.FUN,
                "UwU-ifies the last message.",
                "uwu [message]",
                new String[] {"uwuize", "uwuify"},
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) {
        String[] args = command.getArgs();
        if(args.length != 0) {
            Message message = channel.getMessageById(args[0]).complete();

            if(message != null) {
                channel.sendMessage(uwuify(GUtil.sanitizeMentions(message))).queue();
                return true;
            }

            channel.sendMessage(uwuify(GUtil.sanitizeMentions(StringUtils.join(args), channel.getGuild()))).queue();
            return true;
        }

        MessageHistory messageHistory = channel.getHistory();
        List<Message> messageList = messageHistory.retrievePast(10).complete();

        for(Message message : messageList) {
            if(!message.getContentDisplay().equalsIgnoreCase(command.getMessage().getContentDisplay())) {
                if (!message.getAuthor().isBot() && !message.isWebhookMessage() && message.getEmbeds().size() == 0) {

                    channel.sendMessage(uwuify(GUtil.sanitizeMentions(message))).queue();
                    return true;
                }
            }
        }

        return true;
    }

    private static final String[] faces = new String[] {"(・`ω´・)", ";;w;;", "owo", "UwU", ">w<", "^w^"};
    public static String uwuify(String input) {
        String v = input;

        // Stolen from honk.moe

        v = v.replaceAll("(?:r|l)", "w");
        v = v.replaceAll("(?:R|L)", "W");
        v = v.replaceAll("n([aeiou])", "ny$1");
        v = v.replaceAll("N([aeiou])", "Ny$1");
        v = v.replaceAll("N([AEIOU])", "Ny$1");
        v = v.replaceAll("ove", "uv");
        v = v.replaceAll("\\!+", " " + faces[(int) Math.floor(Math.random() * faces.length)] + " ");

        return v;
    }
}
