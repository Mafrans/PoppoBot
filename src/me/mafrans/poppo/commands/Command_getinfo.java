package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.objects.Information;
import me.mafrans.poppo.util.web.InformationGetter;
import me.mafrans.poppo.util.SelectionList;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class Command_getinfo implements ICommand {
    @Override
    public String getName() {
        return "getinfo";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                "Gets information from Google.",
                "getinfo <query>",
                Arrays.asList("whats", "what's"),
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

        SelectionList selectionList = new SelectionList("Select result to show.", channel);

        for(Information info : informationList) {
            selectionList.addAlternative(info.getTitle() + " - " + info.getDescription(), new Runnable() {
                @Override
                public void run() {
                    StringBuilder builder = new StringBuilder();

                    if(info.getTitle() != null)
                        builder.append("**" + info.getTitle() + ":**");
                    if(info.getDescription() != null)
                        builder.append("\n\n" + info.getDescription());
                    if(info.getDetailedDescription() != null)
                        builder.append("\n\n```\n" + info.getDetailedDescription() + "\n```");
                    if(info.getUrl() != null)
                        builder.append("\nURL: <" + info.getUrl() + ">");
                    if(info.getReadMoreUrl() != null)
                        builder.append("\nRead More: <" + info.getReadMoreUrl() + ">");

                    selectionList.getMessage().editMessage(builder.toString()).queue();
                }
            });
        }

        selectionList.show(channel);

        return true;
    }
}
