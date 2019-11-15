package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.awt.*;
import java.util.ArrayList;

@Id("commands::leave")
public class Command_leave implements ICommand {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Forces Poppo to stop playing and leave the voice channel.", "leave", null, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        if(!channel.getGuild().getMember(command.getAuthor()).getVoiceState().inVoiceChannel()) {
            channel.sendMessage("\uD83C\uDFB5 You cannot force Poppo to leave unless you are in the same voice channel.").queue();
            return true;
        }
        VoiceChannel voiceChannel = channel.getGuild().getMember(command.getAuthor()).getVoiceState().getChannel();
        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        if(manager.getConnectedChannel() != voiceChannel) {
            channel.sendMessage("\uD83C\uDFB5 You cannot force Poppo to leave unless you are in the same voice channel.").queue();
            return true;
        }

        if(voiceChannel.getMembers().size() == 1) {
            if(Rank.requirePermission(command, Permission.VOICE_MOVE_OTHERS)) {
                return true;
            }
        }

        Main.musicManager.scheduleMap.put(channel.getGuild(), new ArrayList<>());
        manager.closeAudioConnection();

        channel.sendMessage("Okay! Thanks for listening!").queue();

        return true;
    }
}
