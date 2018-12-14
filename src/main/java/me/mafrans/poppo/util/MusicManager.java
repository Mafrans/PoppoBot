package me.mafrans.poppo.util;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.listeners.TrackScheduler;
import me.mafrans.poppo.util.objects.YoutubeVideo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MusicManager {
    public AudioPlayerManager audioPlayerManager;
    public Map<Guild, AudioPlayer> audioPlayerMap;
    public Map<Guild, List<YoutubeVideo>> scheduleMap;
    public Map<Guild, List<Member>> skipMap;
    public MusicManager() {
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        audioPlayerMap = new HashMap<>();
        scheduleMap = new HashMap<>();
        skipMap = new HashMap<>();
    }

    public void joinChannel(TextChannel channel, VoiceChannel voiceChannel) {
        AudioPlayer player = audioPlayerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler(channel, voiceChannel);
        player.addListener(trackScheduler);
        audioPlayerMap.put(voiceChannel.getGuild(), player);
        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        // MySendHandler should be your AudioSendHandler implementation
        manager.setSendingHandler(new AudioPlayerSendHandler(player));
        // Here we finally connect to the target voice channel
        // and it will automatically start pulling the audio from the MySendHandler instance
        manager.openAudioConnection(voiceChannel);
    }

    public void queue(TextChannel textChannel, VoiceChannel voiceChannel, YoutubeVideo youtubeVideo) {
        List<YoutubeVideo> youtubeVideos = new ArrayList<>();
        if(scheduleMap.containsKey(voiceChannel.getGuild())) {
            youtubeVideos = scheduleMap.get(voiceChannel.getGuild());
        }
        youtubeVideos.add(youtubeVideo);
        scheduleMap.put(voiceChannel.getGuild(), youtubeVideos);

        System.out.println(youtubeVideos.size());
        if(youtubeVideos.size() <= 1) {
            playNext(textChannel, voiceChannel);
        }
        else {
            textChannel.sendMessage("\uD83C\uDFB5 Queued: `" + youtubeVideo.getTitle() + "`").queue();
        }
    }

    public YoutubeVideo skip(TextChannel textChannel, VoiceChannel voiceChannel) {
        AudioPlayer audioPlayer = audioPlayerMap.get(voiceChannel.getGuild());
        YoutubeVideo video = scheduleMap.get(voiceChannel.getGuild()).get(0);
        audioPlayer.stopTrack();
        playNext(textChannel, voiceChannel);
        return video;
    }

    public void playNext(TextChannel textChannel, VoiceChannel voiceChannel) {
        AudioPlayer audioPlayer = audioPlayerMap.get(voiceChannel.getGuild());
        System.out.println(scheduleMap.get(voiceChannel.getGuild()));

        if(scheduleMap.get(voiceChannel.getGuild()).size() == 0) {
            System.out.println("Yes");
            return;
        }

        audioPlayerManager.loadItem(scheduleMap.get(voiceChannel.getGuild()).get(0).getVideoUrl(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                System.out.println(1);
                EmbedBuilder embedBuilder = new EmbedBuilder();
                System.out.println(2);
                embedBuilder.setAuthor("Now Playing", null, Main.jda.getSelfUser().getEffectiveAvatarUrl());
                System.out.println(3);
                embedBuilder.setTitle(scheduleMap.get(voiceChannel.getGuild()).get(0).getTitle());
                System.out.println(4);
                String durationStamp = String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(audioTrack.getDuration()),
                        TimeUnit.MILLISECONDS.toSeconds(audioTrack.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(audioTrack.getDuration()))
                );
                System.out.println(5);

                embedBuilder.setDescription("Length: `" + durationStamp + "`\nUploaded By: `" + scheduleMap.get(voiceChannel.getGuild()).get(0).getChannelTitle() + "`");
                System.out.println(6);
                embedBuilder.setThumbnail(scheduleMap.get(voiceChannel.getGuild()).get(0).getImageUrl());
                System.out.println(7);
                embedBuilder.setColor(GUtil.randomColor());
                System.out.println(8);
                MessageEmbed embed = embedBuilder.build();
                System.out.println(9);
                System.out.println(embed);
                System.out.println(10);
                textChannel.sendMessage(embed).queue();
                System.out.println(11);
                audioPlayer.playTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor("Now Playing", null, Main.jda.getSelfUser().getEffectiveAvatarUrl());
                    embedBuilder.setTitle(scheduleMap.get(voiceChannel.getGuild()).get(0).getTitle());
                    String durationStamp = String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(track.getDuration()),
                            TimeUnit.MILLISECONDS.toSeconds(track.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(track.getDuration()))
                    );

                    embedBuilder.setDescription("Length: `" + durationStamp + "`\nUploaded By: `" + scheduleMap.get(voiceChannel.getGuild()).get(0).getChannelTitle() + "`");
                    embedBuilder.setThumbnail(scheduleMap.get(voiceChannel.getGuild()).get(0).getImageUrl());
                    embedBuilder.setColor(GUtil.randomColor());
                    MessageEmbed embed = embedBuilder.build();
                    System.out.println(embed);
                    textChannel.sendMessage(embed).queue();
                    audioPlayer.playTrack(track);
                }
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Huh, we had some issues playing " + scheduleMap.get(voiceChannel.getGuild()).get(0).getVideoUrl()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }
}
