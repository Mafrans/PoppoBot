package me.mafrans.poppo.commands.util;

import net.dv8tion.jda.core.entities.TextChannel;

public interface ICommand {
    public String getName();
    public CommandMeta getMeta();
    public boolean onCommand(Command command, TextChannel channel) throws Exception;
}
