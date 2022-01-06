package fun.lewisdev.deluxehub.module.modules.visual.tablist;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.config.ConfigType;
import fun.lewisdev.deluxehub.module.Module;
import fun.lewisdev.deluxehub.module.ModuleType;
import fun.lewisdev.deluxehub.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TablistManager extends Module {
    private List<UUID> players;
    private int tablistTask;
    private String header;
    private String footer;

    public TablistManager(DeluxeHubPlugin plugin) {
        super(plugin, ModuleType.TABLIST);
    }

    @Override
    public void onEnable() {
        players = new ArrayList<>();

        FileConfiguration config = getConfig(ConfigType.SETTINGS);

        header = String.join("\n", config.getStringList("tablist.header"));
        footer = String.join("\n", config.getStringList("tablist.footer"));

        if (config.getBoolean("tablist.refresh.enabled")) {
            tablistTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new TablistUpdateTask(this), 0L,
                    config.getLong("tablist.refresh.rate"));
        }

        getPlugin().getServer().getScheduler()
                .scheduleSyncDelayedTask(getPlugin(),
                        () -> Bukkit.getOnlinePlayers().stream()
                                .filter(player -> !inDisabledWorld(player.getLocation())).forEach(this::createTablist),
                        20L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(tablistTask);
        Bukkit.getOnlinePlayers().forEach(this::removeTablist);
    }

    public void createTablist(Player player) {
        UUID uuid = player.getUniqueId();
        players.add(uuid);
        updateTablist(uuid);
    }

    public boolean updateTablist(UUID uuid) {
        if (!players.contains(uuid))
            return false;

        Player player = Bukkit.getPlayer(uuid);

        if (player == null)
            return false;

        TablistHelper.sendTabList(player, PlaceholderUtil.setPlaceholders(header, player),
                PlaceholderUtil.setPlaceholders(footer, player));

        return true;
    }

    public void removeTablist(Player player) {
        if (players.contains(player.getUniqueId())) {
            players.remove(player.getUniqueId());
            TablistHelper.sendTabList(player, null, null);
        }
    }

    public List<UUID> getPlayers() {
        return players;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!inDisabledWorld(player.getLocation()))
            createTablist(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeTablist(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World fromWorld = event.getFrom().getWorld();

        if (event.getTo() == null) return;

        World toWorld = event.getTo().getWorld();

        if (toWorld == null) return;
        if (fromWorld == toWorld) return;

        if (inDisabledWorld(toWorld) && players.contains(player.getUniqueId())) {
            removeTablist(player);
            return;
        }

        createTablist(player);
    }
}
