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

import me.zetastormy.akropolis.config.ConfigurationContainer;
import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.config.type.Settings;
import me.zetastormy.akropolis.util.MessagingUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.ItemStackBuilder;
import net.kyori.adventure.text.Component;

public class FightModeManager extends Module implements LifeCycle {
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
    private final ConfigurationContainer<Messages> messagesConfig;

    public FightModeManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.FIGHT_MODE);

        this.messagesConfig = plugin.getConfigManager().getFile(Messages.class);

        this.holdTasks = new HashMap<>();
        this.holdTimers = new HashMap<>();
        this.fighters = new HashSet<>();
    }

    @Override
    public void onEnable() {
        Settings.FightMode conf = getConfig(Settings.class).fightMode();

        Settings.FightMode.Armor armorConfig = conf.armor();
        ItemStack helmet = ItemStackBuilder.getItemStack(armorConfig.helmet()).build();
        ItemStack chestplate = ItemStackBuilder.getItemStack(armorConfig.chestplate()).build();
        ItemStack leggings = ItemStackBuilder.getItemStack(armorConfig.leggings()).build();
        ItemStack boots = ItemStackBuilder.getItemStack(armorConfig.boots()).build();

        Settings.FightMode.HoldDelay holdDelay = conf.holdDelay();
        int activateDelay = holdDelay.activate();
        int deactivateDelay = holdDelay.deactivate();

        Settings.FightMode.Actions actionsConfig = conf.actions();
        List<String> countdownActions = actionsConfig.countdown();
        List<String> activatedActions = actionsConfig.activated();
        List<String> deactivatedActions = actionsConfig.deactivated();

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

            final Messages messages = this.messagesConfig.getConfig();
            MessagingUtil.sendWithReplacement(
                    messages.fightMode().activateDelay(),
                    player,
                    "seconds",
                    timeLeft
            );

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

            final Messages messages = this.messagesConfig.getConfig();
            MessagingUtil.sendWithReplacement(
                    messages.fightMode().deactivateDelay(),
                    player,
                    "seconds",
                    timeLeft
            );

            holdTimers.put(playerUuid, time + 1);
        }, 0L, 20L);

        holdTasks.put(playerUuid, taskId);
    }

    public boolean isValidItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;

        String key = item.getItemMeta()
                        .getPersistentDataContainer()
                        .get(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING);

        return key == null ? false : key.equals("FIGHT_MODE_ITEM");
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
        if (isInFightMode(player.getUniqueId())) return;

        giveArmor(player);
        fighters.add(player.getUniqueId());
        executeActions(player, activatedActions);
    }

    public void disableFightMode(Player player) {
        if (!isInFightMode(player.getUniqueId())) return;

        removeArmor(player);
        fighters.remove(player.getUniqueId());
        executeActions(player, deactivatedActions);
    }

    public boolean isInFightMode(UUID playerUuid) {
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
