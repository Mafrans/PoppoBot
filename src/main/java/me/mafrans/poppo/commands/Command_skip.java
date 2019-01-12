package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.objects.YoutubeVideo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;

public class Command_skip implements ICommand {
    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Votes to skip the current song.", "skip", new ArrayList<>(), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        if(!channel.getGuild().getMember(command.getAuthor()).getVoiceState().inVoiceChannel()) {
            channel.sendMessage("\uD83C\uDFB5 You cannot skip a song you are not currently listening to.").queue();
            return true;
        }
        VoiceChannel voiceChannel = channel.getGuild().getMember(command.getAuthor()).getVoiceState().getChannel();
        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        if(manager.getConnectedChannel() != voiceChannel) {
            channel.sendMessage("\uD83C\uDFB5 You cannot skip a song you are not currently listening to.").queue();
        }

        List<Member> skips = Main.musicManager.skipMap.getOrDefault(channel.getGuild(), new ArrayList<>());
        skips.add(channel.getGuild().getMember(command.getAuthor()));
        Main.musicManager.skipMap.put(channel.getGuild(), skips);

        if(skips.size() >= (voiceChannel.getMembers().size()/2)) {
            System.out.println("if");
            System.out.println(Main.musicManager.scheduleMap.get(channel.getGuild()).size());
            YoutubeVideo video = Main.musicManager.skip(channel, voiceChannel);
            channel.sendMessage("\uD83C\uDFB5 Skipped `" + video.getTitle() + "`").queue();
            System.out.println(Main.musicManager.scheduleMap.get(channel.getGuild()).size());
        }
        else {
            System.out.println("else");
            channel.sendMessage("\uD83C\uDFB5 Added vote for skip. `" + skips.size() + "/" + (voiceChannel.getMembers().size()/2) + "`").queue();
        }

        return true;
    }
}
