package me.mafrans.poppo.commands.util;

import net.dv8tion.jda.core.entities.TextChannel;

public interface ICommand {
    String getName();
    CommandMeta getMeta();
    boolean onCommand(Command command, TextChannel channel) throws Exception;
}
