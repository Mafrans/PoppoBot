package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.objects.Rank;
import me.mafrans.poppo.util.objects.YoutubeVideo;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;

public class Command_forceskip implements ICommand {
    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Skips the current song.", "forceskip", null, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        if(Rank.requirePermission(command, Permission.VOICE_MUTE_OTHERS)) {
            return true;
        }

        if(!channel.getGuild().getMember(command.getAuthor()).getVoiceState().inVoiceChannel()) {
            channel.sendMessage("\uD83C\uDFB5 You cannot skip a song you are not currently listening to.").queue();
            return true;
        }
        VoiceChannel voiceChannel = channel.getGuild().getMember(command.getAuthor()).getVoiceState().getChannel();
        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        if(manager.getConnectedChannel() != voiceChannel) {
            channel.sendMessage("\uD83C\uDFB5 You cannot skip a song you are not currently listening to.").queue();
            return true;
        }

        System.out.println(Main.musicManager.scheduleMap.get(channel.getGuild()).size());
        YoutubeVideo video = Main.musicManager.skip(channel, voiceChannel);
        channel.sendMessage("\uD83C\uDFB5 Skipped `" + video.getTitle() + "`").queue();
        System.out.println(Main.musicManager.scheduleMap.get(channel.getGuild()).size());

        return true;
    }
}
