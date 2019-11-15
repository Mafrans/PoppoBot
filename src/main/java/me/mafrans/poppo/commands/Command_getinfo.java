package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.SelectionList;
import me.mafrans.poppo.util.objects.Information;
import me.mafrans.poppo.util.web.InformationGetter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Id("commands::getinfo")
public class Command_getinfo implements ICommand {
    @Override
    public String getName() {
        return "getinfo";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.WEB,
                "Gets information from Google.",
                "getinfo <query>",
                new String[] {"whats", "what's"},
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) {
        String[] args = command.getArgs();
        if(args.length < 1) return false;

        List<Information> informationList = InformationGetter.getHits(StringUtils.join(args, " "));

        if(informationList == null) {
            channel.sendMessage("Something went wrong when contacting the Google API, please try again later!").queue();
            return true;
        }

        final SelectionList selectionList = new SelectionList("Select result to show.", channel, command.getAuthor());

        for(final Information info : informationList) {
            selectionList.addAlternative(info.getTitle() + " - " + info.getDescription(), () -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(GUtil.randomColor());

                if(info.getTitle() != null) {
                    if(info.getDescription() != null)
                        embedBuilder.setAuthor(info.getTitle() + " (" + info.getDescription() + ")", null, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/512px-Google_%22G%22_Logo.svg.png");
                    else
                        embedBuilder.setAuthor(info.getTitle(), null, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/512px-Google_%22G%22_Logo.svg.png");
                }

                if(info.getDetailedDescription() != null)
                    embedBuilder.addField("Description", info.getDetailedDescription(), false);
                if(info.getUrl() != null)
                    embedBuilder.addField("URL", info.getUrl(), true);
                if(info.getReadMoreUrl() != null)
                    embedBuilder.addField("Read More", info.getReadMoreUrl(), true);

                selectionList.getMessage().delete().queue();
                channel.sendMessage(embedBuilder.build()).queue();
            });
        }

        selectionList.show(channel);

        return true;
    }
}
