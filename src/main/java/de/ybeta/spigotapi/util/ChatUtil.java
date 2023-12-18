package de.ybeta.spigotapi.util;

import de.ybeta.spigotapi.util.gradient.ColorUtil;

import java.util.*;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;

public class ChatUtil {

    private static final int CENTER_PX = 154;

    public static String centerMessage(String message) {
        if (message == null || message.isEmpty()) return "";

        message = color(message).replace("<center>", "").replace("</center>", "");

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;

            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';

            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + message;
    }

    public static String color(String text) {
        if (text == null) return null;
        if (text.isEmpty()) return "";
        return process(text);
    }

    public static List<String> colorList(List<String> list) {
        List<String> colorizedList = new ArrayList<>();
        for (String string : list) {
            colorizedList.add(color(string));
        }
        return colorizedList;
    }

    public static String gradient(String string, String color1, String color2) {
        if (color1.length() != 6 || color2.length() != 6) return string;
        return ColorUtil.process("<GRADIENT:" + color1 + ">" + string + "</GRADIENT:" + color2 + ">");
    }

    public static String solidRGB(String string, String color) {
        if (color.length() != 6) return string;
        return ColorUtil.process("<SOLID:" + color + ">" + string);
    }

    public static String rainbow(String string, float saturation) {
        return ColorUtil.rainbow(string, saturation);
    }

    /*
     * RGB HELP
     *
     * <GRADIENT:color_1>TEXT</GRADIENT:color_2> -> Creates gradient text: color 1 -> color2 || USE #gradient(text, color1, color2)
     * <RAINIBOW%s>TEXT</RAINBOW> -> Creates rainbow with %s saturation || USE #rainbow(text, saturation)
     * <SOLID:color>TEXT -> Creates solid colored Text in RGB color || USE #solidRGB(text, color)
     */
    public static String process(String string) {
        return ColorUtil.process(string);
    }

    public static TextComponent getClickMessage(String text, ClickEvent.Action action, String value) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(color(text)));
        component.setClickEvent(new ClickEvent(action, value));
        return component;
    }

    public static TextComponent getHoverMessage(String text, HoverEvent.Action action, Content content) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(color(text)));
        component.setHoverEvent(new HoverEvent(action, content));
        return component;
    }

    public static TextComponent getHoverClickMessage(String text, ClickEvent.Action clickAction, String clickValue, HoverEvent.Action hoverAction, Content hoverContent) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(color(text)));
        component.setClickEvent(new ClickEvent(clickAction, clickValue));
        component.setHoverEvent(new HoverEvent(hoverAction, hoverContent));
        return component;
    }

}
