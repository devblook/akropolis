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

package team.devblook.akropolis.module.modules.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.PlaceholderUtil;
import team.devblook.akropolis.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener extends Module {
    private ConfigurationSection playersSection;
    private boolean joinQuitMessagesEnabled;
    private String joinMessage;
    private String quitMessage;
    private List<String> joinActions;
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
        // Load config stuff
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        playersSection = getConfig(ConfigType.DATA).getConfigurationSection("players");

        joinQuitMessagesEnabled = config.getBoolean("join_leave_messages.enabled");
        joinMessage = config.getString("join_leave_messages.join_message");
        quitMessage = config.getString("join_leave_messages.quit_message");

        joinActions = config.getStringList("join_events");

        spawnHeal = config.getBoolean("join_settings.heal", false);
        extinguish = config.getBoolean("join_settings.extinguish", false);
        clearInventory = config.getBoolean("join_settings.clear_inventory", false);

        forceJoinFly = config.getBoolean("fly.force_on_join", false);

        fireworkEnabled = config.getBoolean("join_settings.firework.enabled", true);
        if (fireworkEnabled) {
            fireworkFirstJoin = config.getBoolean("join_settings.firework.first_join_only", true);
            fireworkType = config.getString("join_settings.firework.type", "BALL_LARGE");
            fireworkPower = config.getInt("join_settings.firework.power", 1);
            fireworkFlicker = config.getBoolean("join_settings.firework.flicker", true);
            fireworkTrail = config.getBoolean("join_settings.firework.power", true);

            fireworkColors = new ArrayList<>();
            config.getStringList("join_settings.firework.colors").forEach(c -> {
                Color color = TextUtil.getColor(c);
                if (color != null)
                    fireworkColors.add(color);
            });
        }
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    // TODO: Reduce cognitive complexity from 18 to something minor.
    @EventHandler(priority = EventPriority.HIGH)
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

        // Heal the player
        if (spawnHeal) {
            player.setFoodLevel(20);

            AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
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

            if (playersSection != null && playersSection.contains(player.getUniqueId().toString())) {
                boolean hasFly = playersSection.getBoolean(player.getUniqueId() + ".fly");

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
