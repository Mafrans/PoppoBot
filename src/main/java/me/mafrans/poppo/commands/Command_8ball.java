package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Command_8ball implements ICommand {

    private String[] answers = {
            "Yes",                                              // Yes
            "No",                                               // No
            "I don't think so",                                 // No
            "Of course",                                        // Yes
            "Absolutely incorrect",                             // No
            "Not even god could answer that",                   // Maybe
            "Why would I know that?",                           // Maybe
            "Certainly",                                        // Yes
            "Not gonna happen",                                 // No
            "I'm not sure, but I think so",                     // Yes
            "That would be idiotic",                            // No
            "This question is why nobody likes you",            // Maybe
            "This question is as false as your mother's love",  // No
            "I think that's the case",                          // Yes
            "I'd say no, but that would be lying",              // Yes
            "Hahaha, no",                                       // No
            "Poppo says: No",                                   // No
            "I'd consider that a subjective question",          // Maybe

    };

    @Override
    public String getName() {
        return "8ball";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta("Ask a question, get an answer!", "8ball <question>", Arrays.asList("can", "is", "does", "do", "a", "are", "has", "have", "why", "was", "are"), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) {


        String question;
        if(command.getLabel().equalsIgnoreCase("8ball")) {
            question = StringUtils.join(command.getArgs(), " ");
        }
        else {
            question = String.format("%s %s", command.getLabel(), StringUtils.join(command.getArgs(), " "));
        }

        Random random = new Random();
        String answer = answers[random.nextInt(answers.length)];

        if(question.equalsIgnoreCase("is death the only answer?") || question.equalsIgnoreCase("is death the only answer")) {
            answer = "death";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        embedBuilder.setAuthor(command.getMessage().getMember().getEffectiveName() + " asked:", "https://google.com", command.getAuthor().getAvatarUrl());
        embedBuilder.setDescription("\"" + question + "\"");
        embedBuilder.addField("Answer:", answer, false);

        if(!command.getMessage().getAttachments().isEmpty()) {
            embedBuilder.setThumbnail(command.getMessage().getAttachments().get(0).getUrl());
        }
        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }
}
