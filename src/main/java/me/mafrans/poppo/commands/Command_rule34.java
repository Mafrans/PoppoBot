package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.web.Rule34Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command_rule34 implements ICommand {
    @Override
    public String getName() {
        return "rule34";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.NSFW, "Gets a random image from the Genbooru rule34 database.", "rule34 <query>", new String[] {"r34", "genbooru"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(!channel.isNSFW()) {
            channel.sendMessage("This command can only be used in NSFW text channels.").queue();
            return true;
        }
        if(args.length == 0) {
            return false;
        }

        Element[] posts = Rule34Getter.getR34Posts(1, args);

        if(posts[0] == null) {
            channel.sendMessage("Could not find any images with that search query.").queue();
            return true;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setImage(posts[0].getAttribute("file_url"));
        String[] tags = posts[0].getAttribute("tags").split(" ");
        List<String> tagList = new ArrayList<>();
        for(String tag : tags) {
            if(tag != null && !tag.isEmpty()) {
                tagList.add(tag);
            }
        }

        embedBuilder.addField("Tags", StringUtils.join(tagList, ", "), false);
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setFooter("Powered by https://rule34.xxx", null);

        channel.sendMessage(embedBuilder.build()).queue();
        return true;
    }
}
