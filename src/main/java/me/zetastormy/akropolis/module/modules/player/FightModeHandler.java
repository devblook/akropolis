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

package me.zetastormy.akropolis.module.modules.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.ItemStackBuilder;
import net.kyori.adventure.text.Component;

public class FightModeHandler extends Module {
    private final Map<UUID, Integer> holdTasks;
    private final Map<UUID, Integer> holdTimers;
    private final Set<UUID> fighters;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private int activateDelay;
    private int deactivateDelay;
    private List<String> countdownActions;
    private List<String> activatedActions;
    private List<String> deactivatedActions;

    public FightModeHandler(AkropolisPlugin plugin) {
        super(plugin, ModuleType.FIGHT_MODE);

        this.holdTasks = new HashMap<>();
        this.holdTimers = new HashMap<>();
        this.fighters = new HashSet<>();
    }

    @Override
    public void onEnable() {
        ConfigurationSection conf = getConfig(ConfigType.SETTINGS).getConfigurationSection("fight_mode");

        ItemStack helmet = ItemStackBuilder.getItemStack(conf.getConfigurationSection("armor.helmet")).build();
        ItemStack chestplate = ItemStackBuilder.getItemStack(conf.getConfigurationSection("armor.chestplate")).build();
        ItemStack leggings = ItemStackBuilder.getItemStack(conf.getConfigurationSection("armor.leggings")).build();
        ItemStack boots = ItemStackBuilder.getItemStack(conf.getConfigurationSection("armor.boots")).build();
        int activateDelay = conf.getInt("hold_delay.activate");
        int deactivateDelay = conf.getInt("hold_delay.deactivate");
        List<String> countdownActions = conf.getStringList("actions.countdown");
        List<String> activatedActions = conf.getStringList("actions.activated");
        List<String> deactivatedActions = conf.getStringList("actions.deactivated");

        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.activateDelay = activateDelay;
        this.deactivateDelay = deactivateDelay;
        this.countdownActions = countdownActions;
        this.activatedActions = activatedActions;
        this.deactivatedActions = deactivatedActions;
    }

    @Override
    public void onDisable() {
        holdTasks.clear();
        holdTimers.clear();
        fighters.clear();
    }

    public void startActivationTimer(Player player) {
        UUID playerUuid = player.getUniqueId();
        holdTimers.put(playerUuid, 0);

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
            int time = holdTimers.get(playerUuid);
            ItemStack currentItem = player.getInventory().getItemInMainHand();

            if (!isValidItem(currentItem)) {
                cancelHoldTask(playerUuid);
                return;
            }

            if (time >= activateDelay) {
                enableFightMode(player);
                cancelHoldTask(playerUuid);
                return;
            }

            Component timeLeft = Component.text(activateDelay - time);

            executeActions(player, countdownActions);
            Message.FIGHT_MODE_ACTIVATE_DELAY.sendWithReplacement(player, "seconds", timeLeft);
            holdTimers.put(playerUuid, time + 1);
        }, 0L, 20L);

        holdTasks.put(playerUuid, taskId);
    }

    public void startDeactivationTimer(Player player) {
        UUID playerUuid = player.getUniqueId();

        holdTimers.put(playerUuid, 0);

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
            int time = holdTimers.get(playerUuid);
            ItemStack currentItem = player.getInventory().getItemInMainHand();

            if (isValidItem(currentItem)) {
                cancelHoldTask(playerUuid);
                return;
            }

            if (time >= deactivateDelay) {
                disableFightMode(player);
                cancelHoldTask(playerUuid);
                return;
            }

            Component timeLeft = Component.text(deactivateDelay - time);

            executeActions(player, countdownActions);
            Message.FIGHT_MODE_DEACTIVATE_DELAY.sendWithReplacement(player, "seconds", timeLeft);
            holdTimers.put(playerUuid, time + 1);
        }, 0L, 20L);

        holdTasks.put(playerUuid, taskId);
    }

    public boolean isValidItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;

        String key = item.getItemMeta()
                        .getPersistentDataContainer()
                        .get(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING);

        return key.equals("FIGHT_MODE_ITEM");
    }

    public void cancelHoldTask(UUID playerUuid) {
        if (holdTasks.containsKey(playerUuid)) {
            Bukkit.getScheduler().cancelTask(holdTasks.get(playerUuid));
            holdTasks.remove(playerUuid);
        }
    }

    public boolean hasHoldTask(UUID playerUuid) {
        return holdTasks.containsKey(playerUuid);
    }

    public void enableFightMode(Player player) {
        if (isFightModeActive(player.getUniqueId())) return;

        giveArmor(player);
        fighters.add(player.getUniqueId());
        executeActions(player, activatedActions);
    }

    public void disableFightMode(Player player) {
        if (!isFightModeActive(player.getUniqueId())) return;

        removeArmor(player);
        fighters.remove(player.getUniqueId());
        executeActions(player, deactivatedActions);
    }

    public boolean isFightModeActive(UUID playerUuid) {
        return fighters.contains(playerUuid);
    }

    private void giveArmor(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        playerInventory.setHelmet(helmet);
        playerInventory.setChestplate(chestplate);
        playerInventory.setLeggings(leggings);
        playerInventory.setBoots(boots);
    }

    private void removeArmor(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        playerInventory.setHelmet(null);
        playerInventory.setChestplate(null);
        playerInventory.setLeggings(null);
        playerInventory.setBoots(null);
    }
}
