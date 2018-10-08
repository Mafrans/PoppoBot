package me.mafrans.poppo.commands.util;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class Command {
    private String cmd;
    private String[] args;
    private User author;
    private ICommand executor;
    private Message message;
    private String label;

    public Command(String cmd, String[] args, User author, ICommand executor, Message message, String label) {
        this.cmd = cmd;
        this.args = args;
        this.author = author;
        this.executor = executor;
        this.message = message;
        this.label = label;
    }

    public Command() { }

    public String getLabel() {
        return label;
    }

    public ICommand getExecutor() {
        return executor;
    }

    public String getCmd() {
        return cmd;
    }

    public String[] getArgs() {
        return args;
    }

    public User getAuthor() {
        return author;
    }

    public Message getMessage() {
        return message;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setExecutor(ICommand executor) {
        this.executor = executor;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
