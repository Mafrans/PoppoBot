package me.mafrans.poppo.util;

import me.mafrans.javadins.RankedTier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public static Map<RankedTier, String> paladinsTierImages = new HashMap<>();
    static {
        paladinsTierImages.put(RankedTier.QUALIFYING, "");

        paladinsTierImages.put(RankedTier.BRONZE_5, "");
        paladinsTierImages.put(RankedTier.BRONZE_4, "");
        paladinsTierImages.put(RankedTier.BRONZE_3, "");
        paladinsTierImages.put(RankedTier.BRONZE_2, "");
        paladinsTierImages.put(RankedTier.BRONZE_1, "");

        paladinsTierImages.put(RankedTier.SILVER_5, "");
        paladinsTierImages.put(RankedTier.SILVER_4, "");
        paladinsTierImages.put(RankedTier.SILVER_3, "");
        paladinsTierImages.put(RankedTier.SILVER_2, "");
        paladinsTierImages.put(RankedTier.SILVER_1, "");

        paladinsTierImages.put(RankedTier.GOLD_5, "");
        paladinsTierImages.put(RankedTier.GOLD_4, "");
        paladinsTierImages.put(RankedTier.GOLD_3, "");
        paladinsTierImages.put(RankedTier.GOLD_2, "");
        paladinsTierImages.put(RankedTier.GOLD_1, "");

        paladinsTierImages.put(RankedTier.PLATINUM_5, "");
        paladinsTierImages.put(RankedTier.PLATINUM_4, "");
        paladinsTierImages.put(RankedTier.PLATINUM_3, "");
        paladinsTierImages.put(RankedTier.PLATINUM_2, "");
        paladinsTierImages.put(RankedTier.PLATINUM_1, "");

        paladinsTierImages.put(RankedTier.DIAMOND_5, "");
        paladinsTierImages.put(RankedTier.DIAMOND_4, "");
        paladinsTierImages.put(RankedTier.DIAMOND_3, "");
        paladinsTierImages.put(RankedTier.DIAMOND_2, "");
        paladinsTierImages.put(RankedTier.DIAMOND_1, "");

        paladinsTierImages.put(RankedTier.MASTER, "");
        paladinsTierImages.put(RankedTier.GRANDMASTER, "");
    }

    public static Map<me.mafrans.smiteforge.RankedTier, String> smiteConquestTierImages = new HashMap<>();
    static {
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.QUALIFYING, null);

        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_5, "?version=93475b6e48b82ae6514457fd8da9520a");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_4, "?version=93475b6e48b82ae6514457fd8da9520a");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_3, "?version=93475b6e48b82ae6514457fd8da9520a");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_2, "?version=93475b6e48b82ae6514457fd8da9520a");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_1, "?version=93475b6e48b82ae6514457fd8da9520a");

        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_5, "?version=00e1edabd5f12160bf94378255dff037");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_4, "?version=00e1edabd5f12160bf94378255dff037");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_3, "?version=00e1edabd5f12160bf94378255dff037");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_2, "?version=00e1edabd5f12160bf94378255dff037");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_1, "?version=00e1edabd5f12160bf94378255dff037");

        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_5, "?version=40c5c39865a259674c37243d4d130adc");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_4, "?version=40c5c39865a259674c37243d4d130adc");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_3, "?version=40c5c39865a259674c37243d4d130adc");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_2, "?version=40c5c39865a259674c37243d4d130adc");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_1, "?version=40c5c39865a259674c37243d4d130adc");

        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_5, "?version=cb572660ce849d43519e4d624e01dda3");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_4, "?version=cb572660ce849d43519e4d624e01dda3");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_3, "?version=cb572660ce849d43519e4d624e01dda3");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_2, "?version=cb572660ce849d43519e4d624e01dda3");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_1, "?version=cb572660ce849d43519e4d624e01dda3");

        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_5, "?version=a653fcfdae1a0105d5be9be2e1764ab4");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_4, "?version=a653fcfdae1a0105d5be9be2e1764ab4");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_3, "?version=a653fcfdae1a0105d5be9be2e1764ab4");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_2, "?version=a653fcfdae1a0105d5be9be2e1764ab4");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_1, "?version=a653fcfdae1a0105d5be9be2e1764ab4");

        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.MASTER, "?version=81dd947a61fe075518bfec22a099f5b0");
        smiteConquestTierImages.put(me.mafrans.smiteforge.RankedTier.GRANDMASTER, "?version=9e2d8597c1f05ce4403ae80a447c20a8");
    }

    public static Map<me.mafrans.smiteforge.RankedTier, String> smiteDuelTierImages = new HashMap<>();
    static {
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.QUALIFYING, null);

        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_5, "?version=5aea9f36c84799840fd95e75180272e1");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_4, "?version=5aea9f36c84799840fd95e75180272e1");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_3, "?version=5aea9f36c84799840fd95e75180272e1");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_2, "?version=5aea9f36c84799840fd95e75180272e1");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_1, "?version=5aea9f36c84799840fd95e75180272e1");

        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_5, "?version=c566febe744de43eba8b5af7fede624c");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_4, "?version=c566febe744de43eba8b5af7fede624c");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_3, "?version=c566febe744de43eba8b5af7fede624c");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_2, "?version=c566febe744de43eba8b5af7fede624c");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_1, "?version=c566febe744de43eba8b5af7fede624c");

        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_5, "?version=d5ce132114ed8e48e21508eb3836b15e");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_4, "?version=d5ce132114ed8e48e21508eb3836b15e");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_3, "?version=d5ce132114ed8e48e21508eb3836b15e");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_2, "?version=d5ce132114ed8e48e21508eb3836b15e");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_1, "?version=d5ce132114ed8e48e21508eb3836b15e");

        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_5, "?version=626f0761b856203058c32059bee1d226");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_4, "?version=626f0761b856203058c32059bee1d226");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_3, "?version=626f0761b856203058c32059bee1d226");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_2, "?version=626f0761b856203058c32059bee1d226");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_1, "?version=626f0761b856203058c32059bee1d226");

        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_5, "?version=7a2205da801fb3e02683e3a1d4799ae3");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_4, "?version=7a2205da801fb3e02683e3a1d4799ae3");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_3, "?version=7a2205da801fb3e02683e3a1d4799ae3");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_2, "?version=7a2205da801fb3e02683e3a1d4799ae3");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_1, "?version=7a2205da801fb3e02683e3a1d4799ae3");

        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.MASTER, "?version=e88795ccd25fe35fb96c555e89570a92");
        smiteDuelTierImages.put(me.mafrans.smiteforge.RankedTier.GRANDMASTER, "?version=1bb27ff55a9aebd78d625143b099f6fa");
    }

    public static Map<me.mafrans.smiteforge.RankedTier, String> smiteJoustTierImages = new HashMap<>();
    static {
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.QUALIFYING, null);

        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_5, "?version=34c161d7dd2b25bb9fb204f87237615c");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_4, "?version=34c161d7dd2b25bb9fb204f87237615c");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_3, "?version=34c161d7dd2b25bb9fb204f87237615c");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_2, "?version=34c161d7dd2b25bb9fb204f87237615c");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.BRONZE_1, "?version=34c161d7dd2b25bb9fb204f87237615c");

        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_5, "?version=cd01133edf2c7e69dd4c7c80aedd23d8");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_4, "?version=cd01133edf2c7e69dd4c7c80aedd23d8");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_3, "?version=cd01133edf2c7e69dd4c7c80aedd23d8");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_2, "?version=cd01133edf2c7e69dd4c7c80aedd23d8");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.SILVER_1, "?version=cd01133edf2c7e69dd4c7c80aedd23d8");

        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_5, "?version=227e1a5a17b32b01d75aca86f1802cde");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_4, "?version=227e1a5a17b32b01d75aca86f1802cde");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_3, "?version=227e1a5a17b32b01d75aca86f1802cde");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_2, "?version=227e1a5a17b32b01d75aca86f1802cde");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.GOLD_1, "?version=227e1a5a17b32b01d75aca86f1802cde");

        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_5, "?version=64b52b852cecfeb0d89908d1cb2892c0");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_4, "?version=64b52b852cecfeb0d89908d1cb2892c0");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_3, "?version=64b52b852cecfeb0d89908d1cb2892c0");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_2, "?version=64b52b852cecfeb0d89908d1cb2892c0");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.PLATINUM_1, "?version=64b52b852cecfeb0d89908d1cb2892c0");

        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_5, "?version=bb986f49c3a5f9f9a8545c02bb0054f4");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_4, "?version=bb986f49c3a5f9f9a8545c02bb0054f4");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_3, "?version=bb986f49c3a5f9f9a8545c02bb0054f4");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_2, "?version=bb986f49c3a5f9f9a8545c02bb0054f4");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.DIAMOND_1, "?version=bb986f49c3a5f9f9a8545c02bb0054f4");

        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.MASTER, "?version=9aa7b5a1d91e6c967ee524a52d5f83e4");
        smiteJoustTierImages.put(me.mafrans.smiteforge.RankedTier.GRANDMASTER, "?version=86a4e65d8f959f22dd28cd7ae1bdf07d");
    }

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String currentParsedDate(ZoneOffset zoneOffset) {
        return DATE_TIME_FORMAT.format(Date.from(ZonedDateTime.now(zoneOffset).toInstant()));
    }

    public static int[] parseToMinutesSeconds(int seconds) {
        return new int[] {(int) Math.floor(seconds/60f), seconds % 60};
    }

    public static String currentParsedDate() {
        return DATE_TIME_FORMAT.format(new Date());
    }

    public static Color randomColor() {
        Random random = new Random();
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    public static String capitalize(String string) {
        if(string.length() < 2) {
            return "";
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
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

    public static Font getTrueTypeFont(String path) throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream(path));
    }

    public static InputStream getPaladinsTierImage(@NotNull RankedTier rankedTier) {
        System.out.println("images/ranks/paladins/" + capitalize(rankedTier.toString()) + ".png -" + ClassLoader.getSystemResourceAsStream("images/ranks/paladins/" + capitalize(rankedTier.toString()) + ".png"));
        return ClassLoader.getSystemResourceAsStream("images/ranks/paladins/" + capitalize(rankedTier.toString()) + ".png");
    }
    public static InputStream getSmiteConquestTierImage(@NotNull me.mafrans.smiteforge.RankedTier rankedTier) {
        return ClassLoader.getSystemResourceAsStream("images/ranks/smite/conquest/" + capitalize(rankedTier.getTier().toString()) + ".png");
    }
    public static InputStream getSmiteDuelTierImage(@NotNull me.mafrans.smiteforge.RankedTier rankedTier) {
        return ClassLoader.getSystemResourceAsStream("images/ranks/smite/duel/" + capitalize(rankedTier.getTier().toString()) + ".png");
    }
    public static InputStream getSmiteJoustTierImage(@NotNull me.mafrans.smiteforge.RankedTier rankedTier) {
        return ClassLoader.getSystemResourceAsStream("images/ranks/smite/joust/" + capitalize(rankedTier.getTier().toString()) + ".png");
    }

    public static String getPaladinsChampionImage(@NotNull String champion) {
        String imageUrl = null;
        try {
            Document doc = Jsoup.connect(String.format("https://paladins.gamepedia.com/File:Champion_%s_Icon.png", champion.replace(" ", ""))).get();
            imageUrl = doc.getElementById("file").getElementsByTag("a").get(0).attr("href");

            String s = doc.toString(); // For some reason, removing this like breaks the entire code, please don't.
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(imageUrl);
        return imageUrl;
    }
}
