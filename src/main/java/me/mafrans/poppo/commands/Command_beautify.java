package me.mafrans.poppo.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Beautifier;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@Id("commands::beautify")
public class Command_beautify implements ICommand {
    @Override
    public String getName() {
        return "beautify";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Beautifies JSON", "beautify <code>", new String[] {"beautify"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();

        String code = StringUtils.join(ArrayUtils.subarray(args, 1, command.getArgs().length), " ");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();

        String pretty = "";
        String title = "";
        String type = "";
        switch (args[0]) {
            case "javascript":
            case "js":
                Beautifier.Javascript jsBeautifier = new Beautifier.Javascript();
                pretty = jsBeautifier.beautify(code);
                title = "Beautified JavaScript";
                type = "js";
                break;

            case "xml":
                Beautifier.XML xmlBeautifier = new Beautifier.XML();
                pretty = xmlBeautifier.beautify(code);
                title = "Beautified XML";
                type = "xml";
                break;

            case "java":
                Beautifier.Javascript javaBeautifier = new Beautifier.Javascript();
                pretty = javaBeautifier.beautify(code);
                title = "Beautified Java";
                type = "java";
                break;

            case "c":
            case "c++":
                Beautifier.Javascript cBeautifier = new Beautifier.Javascript();
                pretty = cBeautifier.beautify(code);
                title = "Beautified " + args[0].toUpperCase();
                type = "c++";
                break;

            case "css":
            case "cascading":
                Beautifier.CSS cssBeautifier = new Beautifier.CSS();
                pretty = cssBeautifier.beautify(code);
                title = "Beautified CSS";
                type = "css";
                break;

            case "html":
            case "hypertext":
                Beautifier.HTML beautifier = new Beautifier.HTML();
                pretty = beautifier.beautify(code);
                title = "Beautified HTML";
                type = "html";
                break;

            case "json":
                JsonElement je = jp.parse(code);
                pretty = gson.toJson(je);
                title = "Beautified JSON";
                type = "json";
                break;
        }

        int longest = 0;
        for(String line : pretty.split("\r?\n")) {
            if(line.length() > longest) longest = line.length();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setAuthor(title + addBlankSpaces(Math.min(40, longest)), null, null);
        embedBuilder.setDescription("```" + type + "\n" + pretty + "```");
        channel.sendMessage(embedBuilder.build()).queue();
        return true;
    }

    private String addBlankSpaces(int amount) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < amount; i++) {
            s.append("\u2800");
        }
        return s.toString();
    }
}
