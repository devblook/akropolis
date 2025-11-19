/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2025 DevBlook Team and others
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

package me.zetastormy.akropolis.util.text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.modules.world.SongPlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PlaceholderUtil {
    private static boolean papi = false;
    private static boolean miniplaceholders = false;

    private PlaceholderUtil() {
        throw new UnsupportedOperationException();
    }

    public static Component setPlaceholders(String rawText, Player player) {
        String text = rawText;

        text = text.replace("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()))
                 .replace("<online_max>", String.valueOf(Bukkit.getMaxPlayers()))
                 .replace("<current_song>", getCurrentSong());

        if (player != null) {
            text = text.replace("<player>", player.getName())
                     .replace("<ping>", String.valueOf(player.getPing()))
                     .replace("<world>", player.getWorld().getName())
                     .replace("<location>", formatLocation(player.getLocation()));

            return TextUtil.parse(
                text,
                player,
                (papi) ? papiTag(player) : TagResolver.empty(),
                (miniplaceholders) ? MiniPlaceholders.audienceGlobalPlaceholders() : TagResolver.empty()
            );
        }

        return TextUtil.parse(text);
    }

    private static String formatLocation(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    private static String getCurrentSong() {
        SongPlayerManager songPlayerManager = AkropolisPlugin.getInstance().getSongPlayerManager();
        return songPlayerManager != null ? songPlayerManager.getCurrentSong() : "None";
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
