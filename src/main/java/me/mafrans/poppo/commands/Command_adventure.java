package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.FileUtils;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.Zork;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.File;
import java.io.IOException;

@Id("commands::adventure")
public class Command_adventure implements ICommand {
    @Override
    public String getName() {
        return "adventure";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.UTILITY,
                "Start an epic text adventure.",
                "adventure [quit]",
                null,
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) {
        String[] args = command.getArgs();
        if(args.length > 0 && args[0].equalsIgnoreCase("quit")) {
            Zork.close(channel);

            return true;
        }

        try {
            FileUtils.createResource("Zork.xml", new File("Zork.xml"), Main.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Zork zork = new Zork("Zork.xml", channel);
        zork.register();

        return true;
    }
}
