package me.mafrans.poppo.commands.util;

import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@Data
public class Command {
    private String cmd;
    private String[] args;
    private User author;
    private ICommand executor;
    private Message message;
    private String label;
    private boolean override;

    public Command(String cmd, String[] args, User author, ICommand executor, Message message, String label) {
        this.cmd = cmd;
        this.args = args;
        this.author = author;
        this.executor = executor;
        this.message = message;
        this.label = label;
        this.override = false;
    }

    public Command() { }
}
