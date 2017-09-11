package me.mafrans.poppo.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;

public class Command_ping implements ICommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                "Pings the bot and recieves an answer.",
                "ping",
                null,
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) {
        long start_time = System.nanoTime();

        Message message = channel.sendMessage("pong!").complete(); // Blocks the thread, allowing us to check the time it took to send the message.

        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;

        message.editMessage("pong! (" + Math.round(difference) + "ms)").queue();

        return true;
    }
}
