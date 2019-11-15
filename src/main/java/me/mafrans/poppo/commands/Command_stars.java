package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import me.mafrans.poppo.util.images.ImageBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.*;

import static me.mafrans.poppo.Main.config;

@Id("commands::stars")
public class Command_stars implements ICommand {
    @Override
    public String getName() {
        return "stars";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Shows how many stars you have.", "stars", new String[] {"money", "cash", "star", "balance", "bal"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        String[] args = command.getArgs();
        if(args.length > 1) {
            if (config.debug_users.contains(command.getAuthor().getId()) && args[0].equalsIgnoreCase("set")){

                String uuid = command.getAuthor().getId();
                if(args.length > 2) {
                    if (args[2].length() == 18 && NumberUtils.isDigits(args[2])) {
                        uuid = args[2];
                    }
                    else if (command.getMessage().getMentionedUsers().size() > 0) {
                        uuid = command.getMessage().getMentionedUsers().get(0).getId();
                    }
                    else if (Main.jda.getUsersByName(StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " "), true).size() > 0) {
                        uuid = Main.jda.getUsersByName(StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " "), true).get(0).getId();
                    }
                    else {
                        channel.sendMessage("Could not find a user with that name or id.").queue();
                        return true;
                    }
                }

                DataUser dataUser = Main.userList.getUsersFrom("uuid", uuid).get(0);
                dataUser.setStars(Integer.parseInt(args[1]));
                Main.userList.put(new SQLDataUser(dataUser));
                channel.sendMessage("Set " + Main.jda.getUserById(uuid).getName() + "'s Stars to " + dataUser.getStars()).queue();
                System.out.println(Main.userList.getUsersFrom("uuid", uuid).get(0));

                return true;
            }
        }

        User user = command.getAuthor();

        if(args.length > 0) {
            String uuid;
            if (args[0].length() == 18 && NumberUtils.isDigits(args[2])) {
                uuid = args[0];
            }
            else if (command.getMessage().getMentionedUsers().size() > 0) {
                uuid = command.getMessage().getMentionedUsers().get(0).getId();
            }
            else if (Main.jda.getUsersByName(StringUtils.join(ArrayUtils.subarray(args, 0, args.length), " "), true).size() > 0) {
                uuid = Main.jda.getUsersByName(StringUtils.join(ArrayUtils.subarray(args, 0, args.length), " "), true).get(0).getId();
            }
            else {
                channel.sendMessage("Could not find a user with that name or id.").queue();
                return true;
            }
            if(uuid != null) {
                user = Main.jda.getUserById(uuid);
            }
        }

        int stars = 0;
        if(!Main.userList.getUsersFrom("uuid", command.getAuthor().getId()).isEmpty()) {
            stars = Main.userList.getUsersFrom("uuid", command.getAuthor().getId()).get(0).getStars();
        }

        final Font lato = GUtil.getTrueTypeFont("fonts/Lato-Regular.ttf");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(255, 255,  0));
        embedBuilder.setImage("attachment://star.png");
        embedBuilder.setAuthor(user.getName()+ "'s Stars", null, user.getEffectiveAvatarUrl());
        //embedBuilder.setDescription("You have " + stars + " stars.");

        ImageBuilder imageBuilder = new ImageBuilder(128, 128).setColor(Color.BLACK);
        imageBuilder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imageBuilder.setTextFont(lato);
        imageBuilder.setColor(new Color(255, 255, 255)).addShape(new Rectangle(0, 0, 128, 128), true).setColor(Color.BLACK);
        imageBuilder.addImage(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/Star.png")), (128-90)/2, 5, 90, 90,  0);

        int textWidth = 9999;
        int i = 0;
        while(textWidth > 128) {
            imageBuilder.setTextFont(new Font(lato.getFamily(), Font.PLAIN, 35-i));
            FontMetrics fontMetrics = imageBuilder.getGraphics().getFontMetrics();
            textWidth = fontMetrics.stringWidth(String.valueOf(stars));
            i++;
        }

        imageBuilder.addText(String.valueOf(stars), 128/2-textWidth/2, 123, Font.BOLD, 35-i);

        Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
        channel.sendFile(GUtil.toInputStream(imageBuilder.build(), "png"), "star.png", message).queue();

        return true;
    }
}
