package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.objects.Poll;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if(!command.doOverride() && !command.getMessage().getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
            embedBuilder.setDescription("You need the MESSAGE_MANAGE permission to use this command!");
            embedBuilder.setColor(new Color(175, 0, 0));
            channel.sendMessage(embedBuilder.build()).queue();
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
            System.out.println(poll.getTitle() + " ?= " + title);
            if(poll.getTitle().equalsIgnoreCase(title)) {
                if(poll.getChannel() == channel) {
                    poll.getMessage().delete().queue();

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(GUtil.randomColor());
                    embedBuilder.setThumbnail(poll.getImageUrl());
                    embedBuilder.setTitle("Results from your poll: " + poll.getTitle());
                    embedBuilder.addField(Poll.UP_VOTE + " Up Votes", String.valueOf(poll.getUpVotes()), true);
                    embedBuilder.addField(Poll.DOWN_VOTE + " Down Votes", String.valueOf(poll.getDownVotes()), true);
                    embedBuilder.addField("Total Votes", String.valueOf(poll.getDownVotes() + poll.getUpVotes()), true);

                    int percentageUp = (int)((float)poll.getUpVotes()/(poll.getDownVotes() + poll.getUpVotes()))*100;
                    embedBuilder.addField("Total Score", "**" + percentageUp + "%** Voted Up (" + poll.getUpVotes() + "/" + (poll.getDownVotes() + poll.getUpVotes()) + ")\n**" + ((poll.getDownVotes() + poll.getUpVotes()) > 0 ? (100-percentageUp) : 0) + "%** Voted Down (" + poll.getDownVotes() + "/" + (poll.getDownVotes() + poll.getUpVotes()) + ")", true);

                    poll.getAuthor().openPrivateChannel().complete().sendMessage(embedBuilder.build()).queue();
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
