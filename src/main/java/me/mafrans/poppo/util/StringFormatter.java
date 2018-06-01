package me.mafrans.poppo.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * Created by malte on 2017-09-17.
 */
public class StringFormatter {
    public static String parseLines(String[] lines) {
        return StringUtils.join(lines, "\n");
    }

    public static String arrayToString(Object[] objects, String formatting) {
        StringBuilder builder = new StringBuilder();

        builder.append("[");
        builder.append(String.format(formatting, objects[0].toString()));
        for(Object object : ArrayUtils.subarray(objects, 1, objects.length)) {
            builder.append(String.format(formatting, object.toString()));
        }
        builder.append("]");

        return builder.toString();
    }

    public static String getRandom(int length) {
        String validChars = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ123456789_";

        Random rand = new Random();
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < length; i++) {
            out.append(validChars.charAt(rand.nextInt(validChars.length())));
        }
        return out.toString();
    }

    public static String getRandom(int length, String validChars) {
        Random rand = new Random();
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < length; i++) {
            out.append(validChars.charAt(rand.nextInt(validChars.length())));
        }
        return out.toString();
    }

    public static String getGuildEmote(Guild guild, String emoteName) {
        return guild.getEmotesByName(emoteName, false).get(0).getAsMention();
    }
}
