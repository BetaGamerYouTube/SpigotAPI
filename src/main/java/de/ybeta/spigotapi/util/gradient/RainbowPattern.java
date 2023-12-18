package de.ybeta.spigotapi.util.gradient;

import java.util.regex.Matcher;

public class RainbowPattern implements Pattern {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<RAINBOW([0-9]{1,3})>(.*?)</RAINBOW>");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String saturation = matcher.group(1);
            String content = matcher.group(2);
            if (content.isEmpty()) return "";
            string = string.replace(matcher.group(), ColorUtil.rainbow(content, Float.parseFloat(saturation)));
        }
        return string;
    }

}
