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

package team.devblook.akropolis.module.modules.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigHandler;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.TextUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HologramManager extends Module {
    private Set<Hologram> holograms;
    private ConfigHandler dataConfig;
    private ConfigurationSection hologramsSection;

    public HologramManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.HOLOGRAMS);
    }

    @Override
    public void onEnable() {
        holograms = new HashSet<>();
        dataConfig = getPlugin().getConfigManager().getFile(ConfigType.DATA);
        hologramsSection = getConfig(ConfigType.DATA).getConfigurationSection("holograms");

        if (hologramsSection == null) {
            getPlugin().getLogger().info("No holograms to load!");
            return;
        }

        loadHolograms();
    }

    @Override
    public void onDisable() {
        saveHolograms();
    }

    public void loadHolograms() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            for (String key : hologramsSection.getKeys(false)) {
                List<String> rawLines = hologramsSection.getStringList(key + ".lines");
                List<Component> lines = new ArrayList<>();

                rawLines.forEach(l -> lines.add(TextUtil.parse(l)));

                Location location = (Location) hologramsSection.get(key + ".location");

                if (location == null) continue;

                deleteNearbyHolograms(location);

                createHologram(key, location).setLines(lines);
            }
        }, 40L);
    }

    public void saveHolograms() {
        holograms.forEach(hologram -> {
            dataConfig.get().set("holograms." + hologram.getName() + ".location", hologram.getLocation());

            List<String> lines = new ArrayList<>();

            for (ArmorStand stand : hologram.getStands()) {
                Component standName = stand.customName();

                if (standName != null) {
                    lines.add(TextUtil.raw(standName));
                }
            }

            dataConfig.get().set("holograms." + hologram.getName() + ".lines", lines);
        });

        dataConfig.save();
        removeAllHolograms();
    }

    public Set<Hologram> getHolograms() {
        return holograms;
    }

    public boolean hasHologram(String name) {
        return getHolograms().stream().anyMatch(hologram -> hologram.getName().equalsIgnoreCase(name));
    }

    public Hologram getHologram(String name) {
        return getHolograms().stream().filter(hologram -> hologram.getName().equalsIgnoreCase(name)).findFirst()
                .orElse(null);
    }

    public Hologram createHologram(String name, Location location) {
        Hologram holo = new Hologram(name, location);

        holograms.add(holo);

        return holo;
    }

    public void deleteHologram(String name) {
        Hologram holo = getHologram(name);

        holo.remove();
        holograms.remove(holo);

        if (hologramsSection != null && hologramsSection.get(name) != null) {
            hologramsSection.set(name, null);
            getPlugin().getConfigManager().getFile(ConfigType.DATA).save();
        }
    }

    public void removeAllHolograms() {
        holograms.forEach(Hologram::remove);
        holograms.clear();
    }

    public void deleteNearbyHolograms(Location location) {
        World world = location.getWorld();

        if (world == null) return;

        world.getNearbyEntities(location, 0, 20, 0).stream().filter(entity -> entity instanceof ArmorStand)
                .forEach(Entity::remove);
    }
}
