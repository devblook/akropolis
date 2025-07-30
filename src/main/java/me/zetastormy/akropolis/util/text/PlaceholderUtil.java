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

import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.modules.world.SongPlayerManager;

public class PlaceholderUtil {
    private static boolean papi = false;
    private static boolean miniplaceholders = false;

    private PlaceholderUtil() {
        throw new UnsupportedOperationException();
    }

    private static String formatLocation(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    private static String getCurrentSong() {
        SongPlayerManager songPlayerManager = AkropolisPlugin.getInstance().getSongPlayerManager();
        return songPlayerManager != null ? songPlayerManager.getCurrentSong() : "None";
    }

    /**
     * Pushes a tag resolver onto the stack that inserts (self-closing) the given component for the given name.
     * @param tagResolvers the mutable stack of tag resolvers
     * @param name the name of the tag to resolve (e.g. "player", "ping", etc.)
     * @param component the component to insert when the tag is resolved
     */
    private static void pushTagResolver(final Stack<TagResolver> tagResolvers, final String name, final Component component) {
        tagResolvers.push(TagResolver.resolver(name, (argumentQueue, context)
                -> Tag.selfClosingInserting(component)));
    }

    public static Component setPlaceholders(String rawText, Audience audience) {

        final Stack<TagResolver> tagResolvers = new Stack<>();

        pushTagResolver(tagResolvers, "online", Component.text(Bukkit.getOnlinePlayers().size()));
        pushTagResolver(tagResolvers, "online_max", Component.text(Bukkit.getMaxPlayers()));
        pushTagResolver(tagResolvers, "current_song", Component.text(getCurrentSong()));

        if ((audience instanceof Player player)) {
            pushTagResolver(tagResolvers, "player", Component.text(player.getName()));
            pushTagResolver(tagResolvers, "ping", Component.text(player.getPing()));
            pushTagResolver(tagResolvers, "world", Component.text(player.getWorld().getName()));
            pushTagResolver(tagResolvers, "location", Component.text(formatLocation(player.getLocation())));

            if (papi) {
                tagResolvers.push(papiTag(player));
            }
        }

        if (miniplaceholders && audience != null) {
            tagResolvers.push(MiniPlaceholders.getAudienceGlobalPlaceholders(audience));
        }

        return TextUtil.parse(rawText, TagResolver.resolver(tagResolvers));
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
