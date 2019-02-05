package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Poll;
import me.mafrans.poppo.util.objects.Rank;
import me.mafrans.poppo.util.timedtasks.PollEndDateRunner;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Id("commands::endpoll")
public class Command_endpoll implements ICommand {

    @Override
    public String getName() {
        return "endpoll";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Ends a poll.", "endpoll <title>", new String[] {"endvote", "endvoteoff", "stoppoll", "stopvote", "stopvoteoff"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        if(Rank.requirePermission(command, Permission.MESSAGE_MANAGE)) {
            return true;
        }

        String[] args = command.getArgs();
        if(args.length < 1) {
            return false;
        }

        String title = StringUtils.join(args, " ");
        int amountRemoved = 0;
        List<Poll> openPollsNew = new ArrayList<>(Poll.openPolls);
        for(Poll poll : Poll.openPolls) {
            if(poll.getTitle().equalsIgnoreCase(title)) {
                if(poll.getChannel() == channel) {
                    PollEndDateRunner.endPoll(poll);
                    openPollsNew.remove(poll);

                    amountRemoved++;
                }
            }
        }
        Poll.openPolls = openPollsNew;

        if(amountRemoved == 0) {
            channel.sendMessage("Could not find any polls with that name.").queue();
        }
        else {
            channel.sendMessage("Ended " + amountRemoved + " polls called " + title + " in " + channel.getAsMention()).queue();
        }

        return true;
    }
}
