package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.SelectionList;
import me.mafrans.poppo.util.objects.Definition;
import me.mafrans.poppo.util.web.DefinitionGetter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Command_define implements ICommand {
    @Override
    public String getName() {
        return "define";
    }

    @Override
    public CommandMeta getMeta() {
        String[] ignored = new String[] {"'"};
        return new CommandMeta(
                CommandCategory.WEB,
                "Gets definition data from Pearson or alternatively Urbandictionary.",
                "define <query>",
                null,
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length < 1) return false;

        List<Definition> definitionList = DefinitionGetter.getHits(StringUtils.join(args, "+"));

        if(args[0].equalsIgnoreCase("-u") || args[0].equalsIgnoreCase("-p")) {
            if(args.length < 2) return false;

            if(args[0].equalsIgnoreCase("-u")) { // Urbandictionaries
                definitionList = DefinitionGetter.getUrbanHits(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), "+"));

            }
            else { // Pearson
                definitionList = DefinitionGetter.getPearsonHits(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), "+"));
            }
        }

        if(definitionList == null) {
            channel.sendMessage("Could not find any definition, either there are no such word or there was a problem when contacting the APIs.").queue();
            return true;
        }

        final SelectionList selectionList = new SelectionList("Select result to show.", channel, command.getAuthor());

        for(final Definition definition : definitionList) {
            selectionList.addAlternative(definition.getTitle() + (definition.getType() == null ? "" : " - " + definition.getType()), new Runnable() {
                @Override
                public void run() {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(GUtil.randomColor());

                    if(definition.getTitle() != null) {
                        if (definition.getType() != null)
                            embedBuilder.setTitle(definition.getTitle() + " (" + definition.getType() + ")");
                        else
                            embedBuilder.setTitle(definition.getTitle());
                    }
                    if(definition.getDefinition() != null)
                        embedBuilder.setDescription(definition.getDefinition());
                    if(definition.getExample() != null)
                        embedBuilder.addField("Example", definition.getExample(), false);
                    if(definition.getSource() != null)
                        if(definition.getSource().equalsIgnoreCase("urbandictionary"))
                            embedBuilder.setAuthor("Source: " + definition.getSource(), "https://urbandictionary.com", "https://lh3.ggpht.com/oJ67p2f1o35dzQQ9fVMdGRtA7jKQdxUFSQ7vYstyqTp-Xh-H5BAN4T5_abmev3kz55GH=s180");
                        else
                            embedBuilder.setAuthor("Source: " + definition.getSource(), "https://pearson.com", "https://media.glassdoor.com/sqll/854828/pearson-vue-squarelogo-1490376963914.png");

                    System.out.println(selectionList.getMessage());
                    selectionList.getMessage().delete().queue();
                    channel.sendMessage(embedBuilder.build()).queue();
                }
            });
        }

        selectionList.show(channel);

        return true;
    }
}
