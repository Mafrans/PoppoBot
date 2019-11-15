package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Poll;
import net.dv8tion.jda.core.EmbedBuilder;

@Id("polls::end poll")
public class PollEndDateRunner implements Runnable {
    @Override
    public void run() {
        for(Poll poll : Poll.openPolls) {
            if(poll.getEndDate() != null) {
                if(poll.hasEnded()) {
                    endPoll(poll);
                    Poll.openPolls.remove(poll);
                    break;
                }
            }
        }
    }

    public static void endPoll(Poll poll) {
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
    }
}
