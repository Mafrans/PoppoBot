package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.images.ImageBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;

@Id("commands::color")
public class Command_color implements ICommand {
    @Override
    public String getName() {
        return "color";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Gets the RGB, HEX or HSB variants of a specified color.", "color rgb|hsv|hex <color>", null, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();

        String hex;
        int red;
        int green;
        int blue;
        float hue;
        float saturation;
        float brightness;

        if(args.length == 0) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "hex":
                if(args.length != 2) {
                    channel.sendMessage("Correct usage is: `" + Main.config.command_prefix + "color hex <color>").queue();
                    return true;
                }

                hex = args[1].replace("#", "");
                red = Integer.parseInt(hex.substring(0, 2), 16);
                green = Integer.parseInt(hex.substring(2, 4), 16);
                blue = Integer.parseInt(hex.substring(4, 6), 16);

                float[] hsb1 = Color.RGBtoHSB(red, green, blue, null);
                hue = hsb1[0];
                saturation = hsb1[1];
                brightness = hsb1[2];
                break;

            case "rgb":
                if(args.length != 4) {
                    channel.sendMessage("Correct usage is: `" + Main.config.command_prefix + "color rgb <red> <green> <blue>").queue();
                    return true;
                }

                red = Integer.parseInt(args[1]);
                green = Integer.parseInt(args[2]);
                blue = Integer.parseInt(args[3]);


                float[] hsb2 = Color.RGBtoHSB(red, green, blue, null);
                hue = hsb2[0];
                saturation = hsb2[1];
                brightness = hsb2[2];

                hex = GUtil.addLeadingUntil(Integer.toHexString(red), 2, "0") + GUtil.addLeadingUntil(Integer.toHexString(green), 2, "0") + GUtil.addLeadingUntil(Integer.toHexString(blue), 2, "0");
                break;

            case "hsb": {
                if(args.length != 4) {
                    channel.sendMessage("Correct usage is: `" + Main.config.command_prefix + "color hsb <hue> <saturation> <brightness>").queue();
                    return true;
                }

                hue = Float.parseFloat(args[1]);
                saturation = Float.parseFloat(args[2]);
                brightness = Float.parseFloat(args[3]);


                int rgb = Color.HSBtoRGB(hue, saturation, brightness);
                Color color = new Color(rgb);
                red = color.getRed();
                green = color.getGreen();
                blue = color.getBlue();

                hex = GUtil.addLeadingUntil(Integer.toHexString(red), 2, "0") + GUtil.addLeadingUntil(Integer.toHexString(green), 2, "0") + GUtil.addLeadingUntil(Integer.toHexString(blue), 2, "0");
                break;
            }

            default:
                return false;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        ImageBuilder colorImage = new ImageBuilder(64, 64);
        colorImage.setColor(new Color(red, green, blue)).addShape(new Rectangle(0, 0, 64, 64), true);

        embedBuilder.setAuthor("Information about color #" + hex.toUpperCase(), "https://www.color-hex.com/color/" + hex, "attachment://colorImage.png");
        embedBuilder.setThumbnail("attachment://colorImage.png");
        embedBuilder.addField("HEX", "#" + hex.toUpperCase(), false);
        embedBuilder.addField("RGB", String.format("%d, %d, %d", red, green, blue), false);
        embedBuilder.addField("HSB", String.format("%s, %s, %s", String.valueOf(GUtil.round(hue, 2)), String.valueOf(GUtil.round(saturation, 2)), String.valueOf(GUtil.round(brightness, 2))), false);
        embedBuilder.setColor(new Color(red, green, blue));

        Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();

        channel.sendFile(GUtil.toInputStream(colorImage.build(), "png"), "colorImage.png", message).queue();
        return true;
    }
}
