package me.mafrans.poppo.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUtil {
    private static final String[] christmasPresents = new String[] {
            "street lights",            "coasters",             "brocolli",                 "a candy wrapper",      "a tomato",
            "socks",                    "carrots",              "a flag",                   "a tv",                 "an eraser",
            "teddies",                  "a hair brush",         "a pool stick",             "a photo album",        "a bow",
            "a chalk",                  "a zipper",             "a lotion",                 "a blouse",             "a purse",
            "a pillow",                 "a bottle cap",         "a shoe lace",              "a cell phone",         "a CD",
            "a bottle",                 "a USB drive",          "a cinder block",           "stockings",            "an apple",
            "a white out",              "a piano",              "a soap",                   "a cookie jar",         "a fork",
            "a computer",               "a box",                "a rubber band",            "a canvas",             "a bowl",
            "a couch",                  "a pencil",             "a soy sauce packet",       "a door",               "a chapter book",
            "a bag of packing peanuts", "a doll",               "a towel",                  "a pair of glasses",    "a shirt",
            "a balloon",                "a game of twister",    "a tissue box",             "a sharpie",            "a drill press",
            "a toe ring",               "a clamp",              "a piece of sand paper",    "a greeting card",      "a knife",
            "an outlet",                "a needle",             "a spring",                 "a sofa",               "a picture frame",
            "a cork",                   "a lamp shade",         "speakers",                 "a lamp",               "a thermometer",
            "a model car",              "a rusty nail",         "face wash",                "some twezzers",        "a chocolate bar",
            "a beef",                   "a water bottle",       "a mouse pad",              "a television",         "a candle",
            "a phone",                  "a shovel",             "a wallet",                 "lip gloss",            "clothes",
            "a spoon",                  "a sketch pad",         "a chair",                  "a sandal",             "a piece of bread",
            "a button",                 "a magnet",             "a car",                    "a tire swing",         "a book",
            "fake flowers",             "a drawer",             "a playing card",           "a tree",               "a glow stick",
            "a keyboard",               "a puddle",             "toothpaste",               "a dog",                "sun glasses",
            "eye liner",                "a house",              "a rug",                    "a charger",            "a paint brush",
            "an eraser",                "a bracelet",           "a newspaper",              "pants",                "a bookmark",
            "a lace",                   "a sailboat",           "a camera",                 "a remote",             "a boom box",
            "a hair tie",               "shoes",                "a soda can",               "bananas",              "a helmet",
            "flowers",                  "a bed",                "a controller",             "a key chain",          "a nail file",
            "a scotch tape",            "a monitor",            "a pen",                    "a vase",               "a conditioner",
            "an ipod",                  "tooth picks",          "a sidewalk",               "a washing machine",    "shampoo",
            "nail clippers",            "keys",                 "an ice cube tray",         "a desk",               "a fridge",
            "food",                     "headphones",           "deodorant",                "a sponge",             "an mp3 player",
            "a clay pot",               "a stop sign",          "a radio",                  "a shawl",              "money",
            "a bag",                    "a clock",              "a blanket",                "a leg warmers",        "a plate",
            "a toothbrush",             "a mop",                "a screw",                  "a rubber duck",        "grid paper",
            "a thermostat",             "a checkbook",          "a credit card",            "a watch",              "a buckel",
            "a floor",                  "an air freshener",     "a truck",                  "a perfume",            "a plastic fork",
            "a glass",                  "a slipper",            "a table",                  "a toilet",             "a wagon",
            "a seat belt",              "a ring",               "a paper",                  "a thread",             "a sticky note",
            "a cup",                    "video games",          "a window",                 "a mirror",             "a hanger",
    };

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String currentParsedDate(ZoneOffset zoneOffset) {
        return DATE_TIME_FORMAT.format(Date.from(ZonedDateTime.now(zoneOffset).toInstant()));
    }

    public static String currentParsedDate() {
        return DATE_TIME_FORMAT.format(new Date());
    }

    public static Color randomColor() {
        Random random = new Random();
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
    }

    public static String capitalizeWords(String string) {
        List<String> outList = new ArrayList<>();
        for(String word : string.split(" ")) {
            outList.add(capitalize(word));
        }
        return StringUtils.join(outList, " ");
    }

    public static String[] splitTitleCase(String string) {
        return new ArrayList<>(Arrays.asList(string.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))).toArray(new String[0]);
    }

    public static String joinNatural(Object[] array) {
        return StringUtils.join(ArrayUtils.subarray(array, 0, array.length-1), ", ") + " and " + array[array.length-1];
    }

    public static String getChristmasPresent() {
        Random random = new Random();
        return christmasPresents[random.nextInt(christmasPresents.length)];
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String joinExt(Object[] array, String glue, int wait, String afterWait) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < array.length; i++) {
            if(i > 0 && i % wait == 0) {
                stringBuilder.append(glue + afterWait + array[i]);
            }
            else if(i > 0) {
                stringBuilder.append(glue + array[i]);
            }
            else {
                stringBuilder.append(array[i]);
            }
        }
        return stringBuilder.toString();
    }

    public static String joinNaturalExt(Object[] array, int wait, String afterWait) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < array.length; i++) {
            if(i > 0 && i % wait == 0) {
                if(i == array.length-1) {
                    stringBuilder.append(" and " + afterWait + array[i]);
                }
                else {
                    stringBuilder.append(", " + afterWait + array[i]);
                }
            }
            else if(i > 0) {
                if(i == array.length-1) {
                    stringBuilder.append(" and " + array[i]);
                }
                else {
                    stringBuilder.append(", " + array[i]);
                }
            }
            else {
                stringBuilder.append(array[i]);
            }
        }
        return stringBuilder.toString();
    }


    /// Data Offset Parser - Courtesy of Jerom van der Sar (Prozza)
    public static Date parseDateOffset(String time) {
        Pattern timePattern = Pattern.compile(
                "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find())
        {
            if (m.group() == null || m.group().isEmpty())
            {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++)
            {
                if (m.group(i) != null && !m.group(i).isEmpty())
                {
                    found = true;
                    break;
                }
            }
            if (found)
            {
                if (m.group(1) != null && !m.group(1).isEmpty())
                {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty())
                {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty())
                {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty())
                {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty())
                {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty())
                {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty())
                {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found)
        {
            return null;
        }

        Calendar c = new GregorianCalendar();

        if (years > 0)
        {
            c.add(Calendar.YEAR, years);
        }
        if (months > 0)
        {
            c.add(Calendar.MONTH, months);
        }
        if (weeks > 0)
        {
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        }
        if (days > 0)
        {
            c.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours > 0)
        {
            c.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0)
        {
            c.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0)
        {
            c.add(Calendar.SECOND, seconds);
        }

        return c.getTime();
    }
}
