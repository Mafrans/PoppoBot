package me.mafrans.poppo.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.objects.YoutubeVideo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicListener extends AudioEventAdapter {
    private TextChannel channel;
    private VoiceChannel voiceChannel;
    public MusicListener(TextChannel channel, VoiceChannel voiceChannel) {
        this.channel = channel;
        this.voiceChannel = voiceChannel;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        Guild guild = null;
        for(Guild g : Main.musicManager.audioPlayerMap.keySet()) {
            if(Main.musicManager.audioPlayerMap.get(g) == player) {
                guild = g;
            }
        }

        Main.musicManager.skipMap.put(guild, new ArrayList<>());

        Main.musicManager.scheduleMap.get(guild).remove(0);
        if(Main.musicManager.scheduleMap.get(guild).size() == 0) {
            AudioManager manager = voiceChannel.getGuild().getAudioManager();
            manager.closeAudioConnection();
        }

        if (endReason.mayStartNext) {
            Main.musicManager.playNext(channel, voiceChannel);
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }
}
