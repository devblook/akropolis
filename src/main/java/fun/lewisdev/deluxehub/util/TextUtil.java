package fun.lewisdev.deluxehub.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

public class TextUtil {
    private static final int CENTER_PX = 154;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private TextUtil() {
        throw new UnsupportedOperationException();
    }

    public static Component parse(String message) {
        return MINI_MESSAGE.deserialize(message);
    }

    public static String raw(Component message) {
        return MINI_MESSAGE.serialize(message).replaceAll("\\\\<", "<");
    }

    public static Component parseAndReplace(String message, String pattern, Component replacement) {
        return MINI_MESSAGE.deserialize(message, Placeholder.component(pattern, replacement));
    }

    public static Component replace(Component message, String pattern, Component replacement) {
        return MINI_MESSAGE.deserialize(raw(message), Placeholder.component(pattern, replacement));
    }

    public static Component getCenteredMessage(Component parsedMessage) {
        String rawMessage = raw(parsedMessage);

        if (rawMessage.isEmpty())
            return Component.empty();

        String message = LegacyComponentSerializer.legacyAmpersand().serialize(parsedMessage)
                .replace("<center>", "")
                .replace("</center>", "");

        int messagePxSize = getMessageSize(message);
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(sb + message);
    }

    private static int getMessageSize(final String message) {
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

    public static String joinString(int index, String[] args) {
        StringBuilder builder = new StringBuilder();

        for (int i = index; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        return builder.toString();
    }

    public static Color getColor(String s) {
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
