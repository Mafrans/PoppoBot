package me.mafrans.poppo.util;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class GUtil {
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String currentParsedDate(ZoneOffset zoneOffset) {
        return DATE_TIME_FORMAT.format(Date.from(ZonedDateTime.now(zoneOffset).toInstant()));
    }

    public static String currentParsedDate() {
        return DATE_TIME_FORMAT.format(new Date());
    }
}
