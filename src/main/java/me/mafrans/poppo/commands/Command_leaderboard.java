package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.objects.StarComparator;
import me.mafrans.poppo.util.timedtasks.CleanRunner;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;

public class Command_leaderboard implements ICommand {
    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Shows the star leaderboard.", "leaderboard", new String[] {"leaderboards"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        new CleanRunner().run(); // Clean null entries

        List<DataUser> userList = Main.userList.getAllUsers();
        userList.sort(new StarComparator());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        int amount = Math.min(10, userList.size());
        for(int i = 0; i < amount; i++) {
            DataUser dataUser = userList.get(i);
            User user = Main.jda.getUserById(dataUser.getUuid());
            if(user == null || user.isBot()) {
                amount++;
                System.out.println(i);
                continue;
            }

            if (i == 0) {
                embedBuilder.addField(user.getName(), ":star2: " + dataUser.getStars(), false);
            }
            else if (i < 3) {
                embedBuilder.addField(user.getName(), ":star: " + dataUser.getStars(), false);
            }
            else {
                embedBuilder.addField(user.getName(), String.valueOf(dataUser.getStars()), false);
            }
        }
        embedBuilder.setAuthor("Star Leaderboard", null, "attachment://star.png");
        embedBuilder.setColor(new Color(255, 255,  0));

        Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
        channel.sendFile(ClassLoader.getSystemResourceAsStream("images/Star.png"), "star.png", message).queue();

        return true;
    }
}
