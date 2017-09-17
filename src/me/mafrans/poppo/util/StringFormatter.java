package me.mafrans.poppo.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by malte on 2017-09-17.
 */
public class StringFormatter {
    public static String parseLines(String[] lines) {
        return StringUtils.join(lines, "\n");
    }
}
