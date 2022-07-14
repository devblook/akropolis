package fun.lewisdev.deluxehub.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceholderUtil {
    private static boolean papi = false;

    private PlaceholderUtil() {
        throw new UnsupportedOperationException();
    }

    public static Component setPlaceholders(String rawText, Player player) {
        Component text = TextUtil.parse(rawText);

        if (rawText.contains("<player>") && player != null) {
            text = TextUtil.parseAndReplace(TextUtil.raw(text), "player", player.name());
        }

        if (rawText.contains("<online>")) {
            text = TextUtil.parseAndReplace(TextUtil.raw(text), "online", Component.text(Bukkit.getOnlinePlayers().size()));
        }

        if (rawText.contains("<online_max>")) {
            text = TextUtil.parseAndReplace(TextUtil.raw(text), "online_max", Component.text(Bukkit.getMaxPlayers()));
        }

        if (rawText.contains("<location>") && player != null) {
            Location l = player.getLocation();
            text = TextUtil.parseAndReplace(TextUtil.raw(text), "location", Component.text(l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ()));
        }

        if (rawText.contains("<ping>") && player != null) {
            text = TextUtil.parseAndReplace(TextUtil.raw(text), "ping", Component.text(player.getPing()));
        }

        if (rawText.contains("<world>") && player != null) {
            text = TextUtil.parseAndReplace(TextUtil.raw(text), "world", Component.text(player.getWorld().getName()));
        }

        if (papi && PlaceholderAPI.containsPlaceholders(TextUtil.raw(text)) && player != null) {
            text = TextUtil.parse(PlaceholderAPI.setPlaceholders(player, TextUtil.raw(text)));
        }

        return text;
    }

    public static void setPapiState(boolean papiState) {
        papi = papiState;
    }
}