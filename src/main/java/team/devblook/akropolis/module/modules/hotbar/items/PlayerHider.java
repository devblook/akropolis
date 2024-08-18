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

package team.devblook.akropolis.module.modules.hotbar.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.cooldown.CooldownType;
import team.devblook.akropolis.module.modules.hotbar.HotbarItem;
import team.devblook.akropolis.module.modules.hotbar.HotbarManager;
import team.devblook.akropolis.util.ItemStackBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerHider extends HotbarItem {
    private final int cooldown;
    private final ItemStack hiddenItem;
    private final List<UUID> hidden;

    public PlayerHider(HotbarManager hotbarManager, ItemStack item, int slot, String keyValue) {
        super(hotbarManager, item, slot, keyValue);
        hidden = new ArrayList<>();

        FileConfiguration config = getHotbarManager().getConfig(ConfigType.SETTINGS);
        ItemStack hiddenItem = ItemStackBuilder.getItemStack(config.getConfigurationSection("player_hider.hidden")).build();
        ItemMeta hiddenItemMeta = hiddenItem.getItemMeta();
        PersistentDataContainer hiddenItemContainer = hiddenItemMeta.getPersistentDataContainer();

        hiddenItemContainer.set(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING, keyValue);
        hiddenItem.setItemMeta(hiddenItemMeta);

        this.hiddenItem = hiddenItem;
        cooldown = config.getInt("player_hider.cooldown");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onInteract(Player player) {
        if (!getHotbarManager().tryCooldown(player.getUniqueId(), CooldownType.PLAYER_HIDER, cooldown)) {
            Message.COOLDOWN_ACTIVE.sendFromWithReplacement(player, "time", Component.text(getHotbarManager().getCooldown(player.getUniqueId(), CooldownType.PLAYER_HIDER)));
            return;
        }

        if (!hidden.contains(player.getUniqueId())) {
            for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                player.hidePlayer(pl);
            }

            hidden.add(player.getUniqueId());
            Message.PLAYER_HIDER_HIDDEN.sendFrom(player);

            player.getInventory().setItem(getSlot(), hiddenItem);
        } else {
            for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                player.showPlayer(pl);
            }

            hidden.remove(player.getUniqueId());
            Message.PLAYER_HIDER_SHOWN.sendFrom(player);

            player.getInventory().setItem(getSlot(), getItem());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (hidden.contains(player.getUniqueId())) {
            for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                player.showPlayer(pl);
            }
        }

        hidden.remove(player.getUniqueId());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player playerToHide = event.getPlayer();

        hidden.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) return;

            player.hidePlayer(playerToHide);
        });
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (getHotbarManager().inDisabledWorld(player.getLocation()) && hidden.contains(player.getUniqueId())) {
            for (Player p : Bukkit.getOnlinePlayers())
                player.showPlayer(p);
            hidden.remove(player.getUniqueId());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (hidden.contains(player.getUniqueId())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.showPlayer(p);
            }
            hidden.remove(player.getUniqueId());
        }
    }
}