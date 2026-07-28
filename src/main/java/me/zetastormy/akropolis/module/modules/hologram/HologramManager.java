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

package me.zetastormy.akropolis.module.modules.hologram;

import java.util.*;

import me.zetastormy.akropolis.config.type.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.AkroLocation;
import me.zetastormy.akropolis.util.text.TextUtil;
import net.kyori.adventure.text.Component;

public class HologramManager extends Module implements LifeCycle {
    private Set<Hologram> holograms;
    private Map<String, Data.Hologram> hologramsSection;

    public HologramManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.HOLOGRAMS);
    }

    @Override
    public void onEnable() {
        holograms = new HashSet<>();
        hologramsSection = getConfig(Data.class).getHolograms();

        if (hologramsSection.isEmpty()) {
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> hologramsSection.forEach((hologramName, hologram) -> {
            List<String> rawLines = hologram.getLines();
            List<Component> lines = new ArrayList<>();

            rawLines.forEach(l -> lines.add(TextUtil.parse(l)));

            AkroLocation akroLocation = hologram.getLocation();

            if (akroLocation == null) {
                this.getPlugin().getSLF4JLogger().error(
                    "Couldn't load hologram '{}' because there is no location data",
                    hologramName
                );
                return;
            }

            Location bukkitLocation = akroLocation.toBukkitLocation();

            if (bukkitLocation == null) {
                this.getPlugin().getSLF4JLogger().error(
                    "Couldn't load hologram '{}' because world '{}' is not loaded",
                    hologramName,
                    akroLocation.worldKey()
                );
                return;
            }

            deleteNearbyHolograms(bukkitLocation);

            createHologram(hologramName, bukkitLocation).setLines(lines);
        }), 40L);
    }

    public void saveHolograms() {
        holograms.forEach(hologram -> {
            Data.Hologram storedHologram = new Data.Hologram(this.getLines(hologram), AkroLocation.fromBukkitLocation(hologram.getLocation()));
            hologramsSection.put(hologram.getName(), storedHologram);
        });
        removeAllHolograms();
    }

    private List<String> getLines(Hologram hologram) {
        List<String> lines = new ArrayList<>();

        for (ArmorStand stand : hologram.getStands()) {
            Component standName = stand.customName();

            if (standName != null) {
                lines.add(TextUtil.raw(standName));
            }
        }

        return lines;
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
            hologramsSection.put(name, null);
            getPlugin().getConfigManager().getFile(Data.class).save(this.getConfigurationExecutorService());
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
