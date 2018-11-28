package me.mafrans.poppo.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import java.util.List;
import java.util.Random;

/**
 * Created by malte on 2017-09-17.
 */
public class StringFormatter {
    public static String parseLines(String[] lines) {
        return StringUtils.join(lines, "\n");
    }

    public static String arrayToString(String[] strings) {
        JSONArray jsonArray = new JSONArray();

        for(String string : strings) {
            jsonArray.put(string);
        }

        return jsonArray.toString();
    }

    public static String[] stringToArray(String string) {
        if(!string.startsWith("[") || !string.endsWith("]")) {
            return new String[0];
        }

        return new JSONArray(string).toList().toArray(new String[0]);
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
