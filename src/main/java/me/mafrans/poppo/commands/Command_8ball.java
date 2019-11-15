package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.Id;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.util.Random;

@Id("commands::8ball")
public class Command_8ball implements ICommand {

    /**
     * A list of all possible answers the 8ball can give.
     */
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

    /**
     * <p>
     * The command name.
     * Should be lower case, only contain
     * alphanumerical characters and not contain any spaces.
     * </p>
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "8ball";
    }

    /**
     * Command metadata describing the command's properties.
     * @return <p>
     *     A {@link me.mafrans.poppo.commands.util.CommandMeta}
     *     object describing the command's metadata.
     * </p>
     */
    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.FUN,
                "Ask a question, get an answer!",
                "8ball <question>",
                new String[] {
                        "can",
                        "is",
                        "does",
                        "do",
                        "a",
                        "are",
                        "has",
                        "have",
                        "why",
                        "was",
                        "are"
                },
                false);
    }

    /**
     * The event which fires as the command is activated.
     * All command code is written here.
     *
     * @param command The command which was activated.
     * @param channel The channel in which the command was activated.
     * @return {@literal false} if the command usage was not correct.
     */
    @Override
    public boolean onCommand(final Command command, final TextChannel channel) {
        String question;
        if (command.getLabel().equalsIgnoreCase("8ball")) {
            question = StringUtils.join(command.getArgs(), " ");
        } else {
            question = String.format(
                    "%s %s",

                    command.getLabel(),
                    StringUtils.join(command.getArgs(), " ")
            );
        }

        if (question.isEmpty()) {
            return false;
        }

        Random random = new Random();
        String answer = answers[random.nextInt(answers.length)];

        if (
            question.equalsIgnoreCase("is death the only answer?")
            || question.equalsIgnoreCase("is death the only answer")
        ) {
            answer = "death";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(
            new Color(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
            )
        );
        embedBuilder.setAuthor(
            command.getMessage().getMember().getEffectiveName() + " asked:",
            "https://google.com",
            command.getAuthor().getAvatarUrl()
        );
        embedBuilder.setDescription("\"" + question + "\"");
        embedBuilder.addField("Answer:", answer, false);

        if (!command.getMessage().getAttachments().isEmpty()) {
            embedBuilder.setThumbnail(
                command.getMessage().getAttachments().get(0).getUrl()
            );
        }
        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }
}
