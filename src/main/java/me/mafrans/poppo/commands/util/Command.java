package me.mafrans.poppo.commands.util;

import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

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

    public String getCmd() {
        return cmd;
    }

    public String[] getArgs() {
        return args;
    }

    public User getAuthor() {
        return author;
    }

    public ICommand getExecutor() {
        return executor;
    }

    public Message getMessage() {
        return message;
    }

    public String getLabel() {
        return label;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setExecutor(ICommand executor) {
        this.executor = executor;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public boolean doOverride() {
        return override;
    }

    public Command() { }
}
