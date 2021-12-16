package fun.lewisdev.deluxehub.module.modules.hologram;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.config.ConfigType;
import fun.lewisdev.deluxehub.module.Module;
import fun.lewisdev.deluxehub.module.ModuleType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HologramManager extends Module {
    private Set<Hologram> holograms;
    private FileConfiguration dataConfig;
    private ConfigurationSection hologramsSection;

    public HologramManager(DeluxeHubPlugin plugin) {
        super(plugin, ModuleType.HOLOGRAMS);
    }

    @Override
    public void onEnable() {
        holograms = new HashSet<>();
        dataConfig = getConfig(ConfigType.DATA);
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
                List<String> lines = hologramsSection.getStringList(key + ".lines");
                Location location = (Location) hologramsSection.get(key + ".location");

                if (location == null) continue;

                deleteNearbyHolograms(location);

                createHologram(key, location).setLines(lines);
            }
        }, 40L);
    }

    public void saveHolograms() {
        holograms.forEach(hologram -> {
            dataConfig.set("holograms." + hologram.getName() + ".location", hologram.getLocation());
            List<String> lines = new ArrayList<>();

            for (ArmorStand stand : hologram.getStands()) lines.add(stand.getCustomName());

            dataConfig.set("holograms." + hologram.getName() + ".lines", lines);
        });

        getPlugin().getConfigManager().getFile(ConfigType.DATA).save();
        deleteAllHolograms();
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

        hologramsSection.set(name, null);
        getPlugin().getConfigManager().getFile(ConfigType.DATA).save();
    }

    public void deleteAllHolograms() {
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
