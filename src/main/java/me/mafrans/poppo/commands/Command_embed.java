package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Id("commands::embed")
public class Command_embed implements ICommand {
    @Override
    public String getName() {
        return "embed";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.UTILITY,
                "Create an embed from JSON information.",
                "embed <json>",
                null,
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) {
        String[] args = command.getArgs();

        if(Rank.requirePermission(command, Permission.MESSAGE_MANAGE)) {
            return true;
        }
        if(args.length == 0) {
            return false;
        }

        JSONObject object = new JSONObject(StringUtils.join(args, " "));
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            if(object.has("title")) {
                if(object.get("title") instanceof JSONObject) {
                    JSONObject title = object.getJSONObject("title");
                    embedBuilder.setTitle(title.getString("name"), title.getString("url"));
                }
                else {
                    embedBuilder.setTitle(object.getString("title"));
                }
            }

            if(object.has("author")) {
                JSONObject author = object.getJSONObject("author");
                if(author.has("name")) {
                    if(author.has("url")) {
                        if(author.has("icon")) {
                            embedBuilder.setAuthor(author.getString("name"), author.getString("url"), author.getString("icon"));
                        }
                        else {
                            embedBuilder.setAuthor(author.getString("name"), author.getString("url"));
                        }
                    }
                    else {
                        embedBuilder.setAuthor(author.getString("name"));
                    }
                }
            }

            if(object.has("description")) {
                embedBuilder.setDescription(object.getString("description"));
            }

            if(object.has("thumbnail")) {
                embedBuilder.setThumbnail(object.getString("thumbnail"));
            }

            if(object.has("image")) {
                embedBuilder.setImage(object.getString("image"));
            }

            if(object.has("fields")) {
                JSONArray fields = object.getJSONArray("fields");
                for(int i = 0; i < fields.length(); i++) {
                    JSONObject field = fields.getJSONObject(i);

                    if(field.has("name") && field.has("content")) {
                        embedBuilder.addField(field.getString("name"), field.getString("content"), field.has("inline") && field.getBoolean("inline"));
                    }
                    else {
                        embedBuilder.addBlankField(field.has("inline") && field.getBoolean("inline"));
                    }
                }
            }
        }
        catch(JSONException e) {
            command.getMessage().getChannel().sendMessage("Could not parse JSON syntax.").queue();
        }

        return true;
    }
}
