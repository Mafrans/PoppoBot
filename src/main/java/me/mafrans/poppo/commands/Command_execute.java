package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.FileUtils;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.Map;

public class Command_execute implements ICommand {
    @Override
    public String getName() {
        return "execute";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Executes JavaScript code", "execute <code>", new String[] {"exec", "js", "javascript"}, false);
    }

    private static Map<String, String> forbidden = new HashMap<>();
    static {
        forbidden.put("for", "For security purposes, You may not have loops in your code.");
        forbidden.put("while", "For security purposes, You may not have loops in your code.");
        forbidden.put("function", "For security purposes, you may not define functions in your code.");
        forbidden.put("timeout", "For security purposes, you may not use timeouts in your code.");
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        ScriptEngine engine = GUtil.getScriptEngine("nashorn");

        String code = StringUtils.join(command.getArgs(), " ");

        if(code.replace(" ", "").length() == 0) return false;

        channel.getIterableHistory().complete().toArray();
        String javascript = FileUtils.readStream(ClassLoader.getSystemResourceAsStream("eval.js"));

        if(Main.config.debug_users.contains(command.getAuthor().getId())) {
            engine.put("guild", command.getMessage().getGuild());
            engine.put("author", command.getAuthor());
            engine.put("message", command.getMessage());
            engine.put("command", command);
            engine.put("channel", channel);
            engine.put("jda", Main.jda);
        }

        if (!Main.config.overlord_users.contains(command.getAuthor().getId())) {
            for(String key : forbidden.keySet()) {
                if(code.toLowerCase().contains(key.toLowerCase())) {
                    channel.sendMessage(forbidden.get(key)).queue();
                    return true;
                }
            }
        }

        engine.eval(javascript.replace("%eval_code%", code));

        return true;
    }
}
