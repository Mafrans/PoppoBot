package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.SelectionList;
import me.mafrans.poppo.util.objects.YoutubeVideo;
import me.mafrans.poppo.util.timedtasks.PoppoRunnable;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;

public class Command_play implements ICommand {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Searches and plays music from youtube.", "play <query|url>", null, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();

        if(!channel.getGuild().getMember(command.getAuthor()).getVoiceState().inVoiceChannel()) {
            channel.sendMessage("\uD83C\uDFB5 Please enter a voice channel to play music.").queue();
            return true;
        }
        VoiceChannel voiceChannel = channel.getGuild().getMember(command.getAuthor()).getVoiceState().getChannel();

        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        String url = args[0];
        UrlValidator urlValidator = new UrlValidator();
        if(!urlValidator.isValid(url)) {
            SelectionList selectionList = new SelectionList("\uD83C\uDFB5 Select a Video to Play", channel, command.getAuthor());

            if(Main.youtubeSearcher.GetVideos(StringUtils.join(args, " "), 10).length == 0) {
                channel.sendMessage("\uD83C\uDFB5 I couldn't find any videos while searching for that query.").queue();
                return true;
            }
            for(YoutubeVideo youtubeVideo : Main.youtubeSearcher.GetVideos(StringUtils.join(args, " "), 10)) {
                selectionList.addAlternative(youtubeVideo.getTitle(), new PoppoRunnable(new Object[]{youtubeVideo, channel, voiceChannel}) {
                    @Override
                    public void run() {
                        YoutubeVideo video = (YoutubeVideo) arguments[0];
                        TextChannel textChannel = (TextChannel) arguments[1];
                        VoiceChannel voiceChannel = (VoiceChannel) arguments[2];

                        if(!manager.isConnected()) {
                            Main.musicManager.joinChannel(textChannel, voiceChannel);
                        }
                        Main.musicManager.queue(textChannel, voiceChannel, video);
                        selectionList.getMessage().delete().queue();
                    }
                });
            }
            selectionList.show(channel);

            return true;
        }

        if(!manager.isConnected()) {
            Main.musicManager.joinChannel(channel, voiceChannel);
        }

        YoutubeVideo video = Main.youtubeSearcher.GetVideos(args[0], 1)[0];
        Main.musicManager.queue(channel, voiceChannel, video);

        return true;
    }
}
