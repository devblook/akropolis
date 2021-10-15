package fun.lewisdev.deluxehub.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderUtil {
    private static boolean papi = false;

    private PlaceholderUtil() {
        throw new UnsupportedOperationException();
    }

    public static String setPlaceholders(String text, Player player) {
        if (text.contains("%player%") && player != null) {
            text = text.replace("%player%", player.getName());
        }

        if (text.contains("%online%")) {
            text = text.replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        }

        if (text.contains("%online_max%")) {
            text = text.replace("%online_max%", String.valueOf(Bukkit.getServer().getMaxPlayers()));
        }

        if (text.contains("%location%") && player != null) {
            Location l = player.getLocation();
            text = text.replace("%location%", l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
        }

        if (text.contains("%ping%") && player != null) {
            text = text.replace("%ping%", String.valueOf(player.getPing()));
        }

        if (text.contains("%world%") && player != null) {
            text = text.replace("%world%", player.getWorld().getName());
        }

        if (papi && player != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    public static void setPapiState(boolean papiState) {
        papi = papiState;
    }

    public static boolean getPapiState() {
        return papi;
    }
}