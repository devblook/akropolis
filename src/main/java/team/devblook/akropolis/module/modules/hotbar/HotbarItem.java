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

package team.devblook.akropolis.module.modules.hotbar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.util.ItemStackBuilder;
import team.devblook.akropolis.util.PlaceholderUtil;
import team.devblook.akropolis.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class HotbarItem implements Listener {
    private final HotbarManager hotbarManager;
    private final ItemStack item;
    private ConfigurationSection configurationSection;
    private final String keyValue;
    private String permission = null;
    private final int slot;
    private boolean allowMovement;

    protected HotbarItem(HotbarManager hotbarManager, ItemStack item, int slot, String keyValue) {
        this.hotbarManager = hotbarManager;
        this.keyValue = keyValue;
        this.slot = slot;

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        container.set(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING, keyValue);
        item.setItemMeta(itemMeta);

        this.item = item;
    }

    public AkropolisPlugin getPlugin() {
        return hotbarManager.getPlugin();
    }

    public HotbarManager getHotbarManager() {
        return hotbarManager;
    }

    public ItemStack getItem() {
        return item;
    }

    protected abstract void onInteract(Player player);

    public String getKeyValue() {
        return keyValue;
    }

    public int getSlot() {
        return slot;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setAllowMovement(boolean allowMovement) {
        this.allowMovement = allowMovement;
    }

    public void setConfigurationSection(ConfigurationSection configurationSection) {
        this.configurationSection = configurationSection;
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }

    public void giveItem(Player player) {
        if (permission != null && !player.hasPermission(permission))
            return;

        ItemStack newItem = item.clone();

        if (getConfigurationSection() != null && getConfigurationSection().contains("username")) {
            String skullName = TextUtil.raw(PlaceholderUtil
                    .setPlaceholders(getConfigurationSection().getString("username", player.getName()), player));
            OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(skullName);

            newItem = new ItemStackBuilder(newItem).setSkullOwner(skullPlayer).build();
        }

        player.getInventory().setItem(slot, newItem);
    }

    public void removeItem(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInSlot = inventory.getItem(slot);

        if (itemInSlot == null) return;

        PersistentDataContainer container = itemInSlot.getItemMeta().getPersistentDataContainer();
        String keyValueInItem = container.get(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING);

        if (keyValueInItem != null && keyValueInItem.equals(keyValue)) {
            inventory.remove(itemInSlot);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!allowMovement) return;

        Player player = (Player) event.getWhoClicked();

        if (getHotbarManager().inDisabledWorld(player.getLocation())) return;

        List<ItemStack> items = new ArrayList<>();
        items.add(event.getCurrentItem());
        items.add(event.getCursor());
        items.add((event.getClick() == ClickType.NUMBER_KEY) ? player.getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem());

        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) continue;

            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            String keyValueInItem = container.get(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING);

            if (keyValueInItem != null && keyValueInItem.equals(keyValue)) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void hotbarItemInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        if (getHotbarManager().inDisabledWorld(player.getLocation()) || itemInHand.getType() == Material.AIR) {
            return;
        }

        PersistentDataContainer container = itemInHand.getItemMeta().getPersistentDataContainer();
        String keyValueInItem = container.get(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING);

        if (keyValueInItem == null || !keyValueInItem.equals(keyValue)) return;

        onInteract(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void hotbarPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!getHotbarManager().inDisabledWorld(player.getLocation()))
            giveItem(player);
    }

    @EventHandler
    public void hotbarPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!getHotbarManager().inDisabledWorld(player.getLocation()))
            removeItem(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void hotbarWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (getHotbarManager().inDisabledWorld(player.getLocation())) {
                removeItem(player);
            } else {
                giveItem(player);
            }
        }, 5L);
    }

    @EventHandler
    public void hotbarPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!getHotbarManager().inDisabledWorld(player.getLocation()))
            giveItem(player);
    }
}
