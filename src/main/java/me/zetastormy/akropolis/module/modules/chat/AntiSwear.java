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

package me.zetastormy.akropolis.module.modules.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class AntiSwear extends Module {
    private List<String> blockedWords;

    public AntiSwear(AkropolisPlugin plugin) {
        super(plugin, ModuleType.ANTI_SWEAR);
    }

    @Override
    public void onEnable() {
        blockedWords = getConfig(ConfigType.SETTINGS).getStringList("anti_swear.blocked_words");
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(Permissions.ANTI_SWEAR_BYPASS.getPermission()))
            return;

        Component message = event.originalMessage();

        for (String word : blockedWords) {
            if (TextUtil.raw(message).contains(word.toLowerCase())) {
                event.setCancelled(true);
                Message.ANTI_SWEAR_WORD_BLOCKED.sendFrom(player);

                Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.hasPermission(Permissions.ANTI_SWEAR_NOTIFY.getPermission())).forEach(p -> p.sendMessage(TextUtil.replace(TextUtil.replace(Message.ANTI_SWEAR_ADMIN_NOTIFY.toComponent(), "player", player.name()), "word", message)));

                return;
            }
        }
    }
}
