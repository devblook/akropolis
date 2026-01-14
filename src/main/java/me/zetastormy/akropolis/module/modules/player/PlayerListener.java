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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.zetastormy.akropolis.config.type.Data;
import me.zetastormy.akropolis.config.type.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.text.PlaceholderUtil;
import me.zetastormy.akropolis.util.text.TextUtil;
import net.kyori.adventure.text.Component;

public class PlayerListener extends Module implements LifeCycle {
    private Map<UUID, Data.PlayerData> playersSection;
    private boolean joinQuitMessagesEnabled;
    private String joinMessage;
    private String quitMessage;
    private List<String> joinActions;
    private int focusedSlot;
    private boolean spawnHeal;
    private boolean extinguish;
    private boolean clearInventory;
    private boolean fireworkEnabled;
    private boolean fireworkFirstJoin;
    private boolean fireworkFlicker;
    private boolean fireworkTrail;
    private int fireworkPower;
    private String fireworkType;
    private List<Color> fireworkColors;
    private boolean forceJoinFly;

    public PlayerListener(AkropolisPlugin plugin) {
        super(plugin, ModuleType.PLAYER_LISTENER);
    }

    @Override
    public void onEnable() {
        Settings config = getConfig(Settings.class);
        Settings.JoinLeaveMessages joinLeaveMessagesConfig = config.joinLeaveMessages();
        Settings.JoinSettings joinSettings = config.joinSettings();
        Settings.JoinSettings.Firework fireworkSettings = joinSettings.firework();
        Settings.Fly flySettings = config.fly();

        this.playersSection = getConfig(Data.class).getPlayers();

        this.joinQuitMessagesEnabled = joinLeaveMessagesConfig.enabled();
        this.joinMessage = joinLeaveMessagesConfig.joinMessage();
        this.quitMessage = joinLeaveMessagesConfig.quitMessage();

        this.joinActions = config.joinEvents();

        this.focusedSlot = joinSettings.focusedSlot();
        this.spawnHeal = joinSettings.heal();
        this.extinguish = joinSettings.extinguish();
        this.clearInventory = joinSettings.clearInventory();

        this.forceJoinFly = flySettings.forceOnJoin();

        this.fireworkEnabled = fireworkSettings.enabled();
        if (this.fireworkEnabled) {
            this.fireworkFirstJoin = fireworkSettings.firstJoinOnly();
            this.fireworkType = fireworkSettings.type();
            this.fireworkPower = fireworkSettings.power();
            this.fireworkFlicker = fireworkSettings.flicker();
            this.fireworkTrail = fireworkSettings.trail();

            if (this.fireworkPower > 255) {
                this.getPlugin().getLogger()
                        .warning("Configured firework power is greater than 255, check your configuration.");
                this.fireworkPower = 255;
            } else if (this.fireworkPower < 0) {
                this.getPlugin().getLogger()
                        .warning("Configured firework power is lower than 0, check your configuration.");
                this.fireworkPower = 0;
            }

            this.fireworkColors = new ArrayList<>();
            fireworkSettings.colors().forEach(c -> {
                Color color = TextUtil.getColor(c);
                if (color != null)
                    fireworkColors.add(color);
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation())) return;

        // Join message handling
        if (joinQuitMessagesEnabled) {
            if (joinMessage.isEmpty())
                event.joinMessage(null);
            else {
                Component message = PlaceholderUtil.setPlaceholders(joinMessage, player);
                event.joinMessage(message);
            }
        }

        if (focusedSlot != -1) player.getInventory().setHeldItemSlot(focusedSlot);

        // Heal the player
        if (spawnHeal) {
            player.setFoodLevel(20);

            Attribute legacyMaxHealth = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.max_health"));
            Attribute maxHealthAttribute = legacyMaxHealth;

            if (legacyMaxHealth == null) maxHealthAttribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("max_health"));
            if (maxHealthAttribute == null) return;

            AttributeInstance maxHealth = player.getAttribute(maxHealthAttribute);
            if (maxHealth == null) return;

            player.setHealth(maxHealth.getBaseValue());
        }

        // Extinguish
        if (extinguish) player.setFireTicks(0);

        // Clear the player inventory
        if (clearInventory) player.getInventory().clear();

        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            // Join events
            executeActions(player, joinActions);

            if (playersSection != null && playersSection.containsKey(player.getUniqueId())) {
                boolean hasFly = playersSection.get(player.getUniqueId()).getFly();

                player.setAllowFlight(hasFly);
                player.setFlying(hasFly);
            } else if (forceJoinFly && player.hasPermission(Permissions.COMMAND_FLIGHT.getPermission())) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }

            // Firework
            if (fireworkEnabled) {
                if (fireworkFirstJoin) {
                    if (!player.hasPlayedBefore()) spawnFirework(player);
                } else {
                    spawnFirework(player);
                }
            }
        }, 3L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        if (joinQuitMessagesEnabled) {
            if (quitMessage.isEmpty())
                event.quitMessage(Component.empty());
            else {
                Component message = PlaceholderUtil.setPlaceholders(quitMessage, player);
                event.quitMessage(message);
            }
        }

        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public void spawnFirework(Player player) {
        Firework f = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fm = f.getFireworkMeta();

        fm.addEffect(FireworkEffect.builder().flicker(fireworkFlicker).trail(fireworkTrail)
                .with(FireworkEffect.Type.valueOf(fireworkType)).withColor(fireworkColors).build());
        fm.setPower(fireworkPower);
        f.setFireworkMeta(fm);
    }
}
