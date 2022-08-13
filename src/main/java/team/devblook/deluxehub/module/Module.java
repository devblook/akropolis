package team.devblook.deluxehub.module;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.config.ConfigType;
import team.devblook.deluxehub.cooldown.CooldownManager;
import team.devblook.deluxehub.cooldown.CooldownType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Module implements Listener {
    private final DeluxeHubPlugin plugin;
    private final ModuleType moduleType;
    private List<String> disabledWorlds;
    private final CooldownManager cooldownManager;

    protected Module(DeluxeHubPlugin plugin, ModuleType type) {
        this.plugin = plugin;
        this.moduleType = type;
        this.cooldownManager = plugin.getCooldownManager();
        this.disabledWorlds = new ArrayList<>();
    }

    public void setDisabledWorlds(List<String> disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    public DeluxeHubPlugin getPlugin() {
        return plugin;
    }

    public boolean inDisabledWorld(Location location) {
        World world = location.getWorld();

        if (world == null) return false;

        return disabledWorlds.contains(world.getName());
    }

    public boolean inDisabledWorld(World world) {
        return disabledWorlds.contains(world.getName());
    }

    public boolean tryCooldown(UUID uuid, CooldownType type, long delay) {
        return cooldownManager.tryCooldown(uuid, type, delay);
    }

    public long getCooldown(UUID uuid, CooldownType type) {
        return (cooldownManager.getCooldown(uuid, type) / 1000);
    }

    public FileConfiguration getConfig(ConfigType type) {
        return getPlugin().getConfigManager().getFile(type).get();
    }

    public void executeActions(Player player, List<String> actions) {
        getPlugin().getActionManager().executeActions(player, actions);
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
