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

package team.devblook.akropolis.module.modules.chat;

import com.cryptomorin.xseries.XSound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.PlaceholderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoBroadcast extends Module implements Runnable {
    private Map<Integer, List<String>> broadcasts;
    private int broadcastTask = 0;
    private int count = 0;
    private int size = 0;
    private int requiredPlayers = 0;
    private Sound sound = null;
    private double volume;
    private double pitch;

    public AutoBroadcast(AkropolisPlugin plugin) {
        super(plugin, ModuleType.ANNOUNCEMENTS);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);

        broadcasts = new HashMap<>();
        int announcementsCount = 0;
        ConfigurationSection announcementsSettings = config.getConfigurationSection("announcements");

        if (announcementsSettings == null) {
            getPlugin().getLogger().severe("Announcement settings configuration section is missing!");
            return;
        }

        ConfigurationSection announcements = announcementsSettings.getConfigurationSection("announcements");

        if (announcements == null) {
            getPlugin().getLogger().severe("Announcements are missing in the configuration!");
            return;
        }

        for (String key : announcements.getKeys(false)) {
            broadcasts.put(announcementsCount, announcements.getStringList(key));
            announcementsCount++;
        }

        if (announcementsSettings.getBoolean("sound.enabled")) {
            String soundValue = announcementsSettings.getString("sound.value");

            if (soundValue != null) {
                XSound.matchXSound(soundValue).ifPresent(s -> sound = s.parseSound());
                volume = announcementsSettings.getDouble("sound.volume");
                pitch = announcementsSettings.getDouble("sound.pitch");
            }
        }

        requiredPlayers = announcementsSettings.getInt("required_players", 0);

        size = broadcasts.size();
        if (size > 0) {
            broadcastTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), this, 60L,
                    announcementsSettings.getLong("delay") * 20);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(broadcastTask);
    }

    @Override
    public void run() {
        if (count == size) count = 0;
        if (!(count < size && Bukkit.getOnlinePlayers().size() >= requiredPlayers)) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (inDisabledWorld(player.getLocation())) continue;

            broadcasts.get(count).forEach(message -> {
                Component parsedMessage = PlaceholderUtil.setPlaceholders(message, player);

                player.sendMessage(parsedMessage);
            });

            if (sound != null) player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
        }

        count++;
    }
}
