/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2024 DevBlook Team and others
 *
 * Akropolis free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akropolis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akropolis. If not, see <http://www.gnu.org/licenses/>.
 */

package team.devblook.akropolis.util;

import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceholderUtil {
    private static boolean papi = false;
    private static boolean miniplaceholders = false;

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

        if (papi && player != null) {
            text = TextUtil.parse(TextUtil.raw(text), papiTag(player));
        }

        if (miniplaceholders && player != null) {
            text = TextUtil.parse(TextUtil.raw(text), MiniPlaceholders.getAudienceGlobalPlaceholders(player));
        }

        return text;
    }

    @SuppressWarnings("deprecation")
    public static TagResolver papiTag(Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();
            String parsedPlaceholder = TextUtil.raw(LegacyComponentSerializer
                    .legacySection()
                    .deserialize(ChatColor.translateAlternateColorCodes('&',
                            PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%'))));

            return Tag.selfClosingInserting(TextUtil.parse(parsedPlaceholder));
        });
    }

    public static void setPapiState(boolean papiState) {
        papi = papiState;
    }

    public static void setMPState(boolean miniplaceholdersState) {
        miniplaceholders = miniplaceholdersState;
    }
}