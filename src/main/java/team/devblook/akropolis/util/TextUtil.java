package team.devblook.akropolis.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Color;

public class TextUtil {
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
