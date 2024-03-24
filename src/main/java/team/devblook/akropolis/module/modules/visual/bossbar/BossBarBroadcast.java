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

package team.devblook.akropolis.module.modules.visual.bossbar;

import com.cryptomorin.xseries.XSound;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BossBarBroadcast extends Module implements Runnable {
    private Map<Integer, String> broadcasts;
    private BossBar broadcastBar;
    private int broadcastTask = 0;
    private int count = 0;
    private int size = 0;
    private Sound sound;
    private double volume;
    private double pitch;

    public BossBarBroadcast(AkropolisPlugin plugin) {
        super(plugin, ModuleType.BOSS_BAR_BROADCAST);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);

        broadcasts = new HashMap<>();
        int announcementsCount = 0;
        ConfigurationSection bossBarSettings = config.getConfigurationSection("boss_bar_announcements");

        if (bossBarSettings == null) {
            getPlugin().getLogger().info("Skipping boss bar broadcast, configuration section is missing!");
            return;
        }

        List<String> announcements = bossBarSettings.getStringList("announcements");

        if (announcements.isEmpty()) {
            getPlugin().getLogger().severe("Boss bar announcements are missing in the configuration!");
            return;
        }

        for (String announcement : announcements) {
            broadcasts.put(announcementsCount, announcement);
            announcementsCount++;
        }

        if (bossBarSettings.getBoolean("sound.enabled")) {
            String soundValue = bossBarSettings.getString("sound.value");

            if (soundValue != null) {
                XSound.matchXSound(soundValue).ifPresent(s -> sound = s.parseSound());
                volume = bossBarSettings.getDouble("sound.volume");
                pitch = bossBarSettings.getDouble("sound.pitch");
            }
        }

        size = broadcasts.size();
        if (size > 0) {
            Component firstBroadcast = TextUtil.parse(broadcasts.get(0));
            this.broadcastBar = BossBar.bossBar(firstBroadcast, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);
            count++;

            broadcastTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), this,
                    60L, bossBarSettings.getLong("delay") * 20);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }

    @Override
    public void run() {
        if (count == size) count = 0;
        if (!(count < size)) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (inDisabledWorld(player.getLocation())) {
                player.activeBossBars().forEach(b -> {
                    if (b.equals(broadcastBar)) {
                        player.hideBossBar(broadcastBar);
                    }
                });

                continue;
            }

            Component parsedMessage = TextUtil.parse(broadcasts.get(count));
            broadcastBar.name(parsedMessage);

            if (sound != null) player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
        }

        count++;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation())) return;

        player.showBossBar(broadcastBar);
    }
}
