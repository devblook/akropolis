package fun.lewisdev.deluxehub.util;

import com.cryptomorin.xseries.ReflectionUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    private final int centerPx;
    private final Pattern hexPattern;
    private final boolean hexUse;

    public TextUtil() {
        this.centerPx = 154;
        this.hexPattern = Pattern.compile("#<([A-Fa-f0-9]){6}>");
        this.hexUse = ReflectionUtils.supports(16);
    }

    public String color(String message) {
        if (hexUse) {
            Matcher matcher = hexPattern.matcher(message);

            while (matcher.find()) {
                String hexString = matcher.group();

                hexString = "#" + hexString.substring(2, hexString.length() - 1);

                ChatColor hex = ChatColor.of(hexString);
                String before = message.substring(0, matcher.start());
                String after = message.substring(matcher.end());

                message = before + hex + after;
                matcher = hexPattern.matcher(message);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getCenteredMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty())
            return "";

        String message = ChatColor.translateAlternateColorCodes('&', rawMessage).replace("<center>", "")
                .replace("</center>", "");

        int messagePxSize = getMessageSize(message);
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = centerPx - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }

    private int getMessageSize(final String message) {
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (final char messageChar : message.toCharArray()) {
            if (messageChar == ChatColor.COLOR_CHAR) {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = messageChar == 'l' || messageChar == 'L';
            } else {
                DefaultFontInfo defaultFont = DefaultFontInfo.getDefaultFontInfo(messageChar);
                messagePxSize += isBold ? defaultFont.getBoldLength() : defaultFont.getLength();
                messagePxSize++;
            }
        }

        return messagePxSize;
    }

    public String joinString(int index, String[] args) {
        StringBuilder builder = new StringBuilder();

        for (int i = index; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        return builder.toString();
    }

    public Color getColor(String s) {
        switch (s.toUpperCase()) {
            case "AQUA":
                return Color.AQUA;
            case "BLACK":
                return Color.BLACK;
            case "BLUE":
                return Color.BLUE;
            case "FUCHSIA":
                return Color.FUCHSIA;
            case "GRAY":
                return Color.GRAY;
            case "GREEN":
                return Color.GREEN;
            case "LIME":
                return Color.LIME;
            case "MAROON":
                return Color.MAROON;
            case "NAVY":
                return Color.NAVY;
            case "OLIVE":
                return Color.OLIVE;
            case "ORANGE":
                return Color.ORANGE;
            case "PURPLE":
                return Color.PURPLE;
            case "RED":
                return Color.RED;
            case "SILVER":
                return Color.SILVER;
            case "TEAL":
                return Color.TEAL;
            case "WHITE":
                return Color.WHITE;
            case "YELLOW":
                return Color.YELLOW;
            default:
                return null;
        }
    }

}
