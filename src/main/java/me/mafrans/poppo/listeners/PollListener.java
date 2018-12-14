package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.SelectionList;
import me.mafrans.poppo.util.objects.Poll;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.HashMap;
import java.util.Map;


public class PollListener extends ListenerAdapter {
    public enum PollState {
        STATE_TITLE,
        STATE_DESCRIPTION,
        STATE_THUMBNAIL,
        ;
    }

    public static Map<Poll, PollState> waitMap = new HashMap<>();

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        for(Poll poll : waitMap.keySet()) {
            if (poll.getAuthor() == event.getAuthor()) {
                PollState state = waitMap.get(poll);

                if(event.getMessage().getContentDisplay().equalsIgnoreCase("cancel")) {
                    waitMap.remove(poll);
                    Poll.openPolls.remove(poll);
                    event.getChannel().sendMessage("Your poll request has been cancelled!").queue();
                    return;
                }

                switch(state) {
                    case STATE_TITLE:
                        poll.setTitle(event.getMessage().getContentDisplay());
                        event.getChannel().sendMessage("Please enter a description for your poll.").queue();
                        waitMap.put(poll, PollState.STATE_DESCRIPTION);
                        break;

                    case STATE_DESCRIPTION:
                        poll.setDescription(event.getMessage().getContentDisplay());
                        event.getChannel().sendMessage("Please upload an image or enter an image url for your poll. Enter 'none' if you do not want an image for your poll.").queue();
                        waitMap.put(poll, PollState.STATE_THUMBNAIL);
                        break;

                    case STATE_THUMBNAIL:
                        waitMap.remove(poll);
                        event.getChannel().sendMessage("Poll is being created.").queue();

                        if(event.getMessage().getContentDisplay().equalsIgnoreCase("none")) {
                            poll.showPoll(poll.getChannel());

                            break;
                        }
                        if(event.getMessage().getEmbeds().size() > 0) {
                            poll.setImageUrl(event.getMessage().getEmbeds().get(0).getUrl());
                            System.out.println(poll.getImageUrl());
                            poll.showPoll(poll.getChannel());
                            break;
                        }
                        UrlValidator urlValidator = new UrlValidator();
                        if(urlValidator.isValid(event.getMessage().getContentDisplay())) {
                            poll.setImageUrl(event.getMessage().getContentDisplay());
                            poll.showPoll(poll.getChannel());
                            break;
                        }

                        poll.showPoll(poll.getChannel());
                }

            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getChannelType() != ChannelType.TEXT) return;
        if(event.getUser().isBot()) return;

        if(Poll.openPolls.isEmpty()) return;
        for(Poll poll : Poll.openPolls) {
            if(poll.getMessage().getId().equals(event.getMessageId())) {
                if(event.getReactionEmote().getName().equals(Poll.UP_VOTE)) {
                    poll.changeUpVotes(1);
                }
                else if(event.getReactionEmote().getName().equals(Poll.DOWN_VOTE)) {
                    poll.changeDownVotes(1);
                }
                else {
                    event.getReaction().removeReaction().queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if(event.getChannelType() != ChannelType.TEXT) return;
        if(event.getUser().isBot()) return;

        if(Poll.openPolls.isEmpty()) return;
        for(Poll poll : Poll.openPolls) {
            if(poll.getMessage().getId().equals(event.getMessageId())) {
                if(event.getReactionEmote().getName().equals(Poll.UP_VOTE)) {
                    poll.changeUpVotes(-1);
                }
                else if(event.getReactionEmote().getName().equals(Poll.DOWN_VOTE)) {
                    poll.changeDownVotes(-1);
                }
            }
        }
    }
}
