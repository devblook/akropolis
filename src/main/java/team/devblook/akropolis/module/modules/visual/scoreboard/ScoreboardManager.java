package team.devblook.akropolis.module.modules.visual.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

import java.util.*;

public class ScoreboardManager extends Module {
    private int scoreTask;
    private Map<UUID, ScoreHelper> players;
    private long joinDelay;
    private long worldDelay;
    private String title;
    private List<String> lines;

    public ScoreboardManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.SCOREBOARD);
    }

    @Override
    public void onEnable() {
        players = new HashMap<>();
        FileConfiguration config = getConfig(ConfigType.SETTINGS);

        title = config.getString("scoreboard.title");
        lines = config.getStringList("scoreboard.lines");

        joinDelay = config.getLong("scoreboard.display_delay.server_enter", 0L);
        worldDelay = config.getLong("scoreboard.display_delay.world_change", 0L);

        if (config.getBoolean("scoreboard.refresh.enabled")) {
            scoreTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new ScoreUpdateTask(this), 0L,
                    config.getLong("scoreboard.refresh.rate"));
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> Bukkit.getOnlinePlayers().stream()
                .filter(player -> !inDisabledWorld(player.getLocation())).forEach(this::createScoreboard), 20L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(scoreTask);
        Bukkit.getOnlinePlayers().forEach(this::removeScoreboard);
    }

    public void createScoreboard(Player player) {
        players.put(player.getUniqueId(), updateScoreboard(player.getUniqueId()));
    }

    public ScoreHelper updateScoreboard(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return null;

        ScoreHelper helper = players.get(player.getUniqueId());

        if (helper == null)
            helper = new ScoreHelper(player);

        helper.setTitle(title);
        helper.setSlotsFromList(lines);
        helper.addPlayer();
        helper.visible(true);

        return helper;

    }

    public void removeScoreboard(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            players.get(player.getUniqueId()).removePlayer();
            players.remove(player.getUniqueId());

            org.bukkit.scoreboard.ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

            if (scoreboardManager == null) return;

            player.setScoreboard(scoreboardManager.getNewScoreboard());
        }
    }

    public boolean hasScore(UUID uuid) {
        return players.containsKey(uuid);
    }

    public Collection<UUID> getPlayers() {
        return players.keySet();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!inDisabledWorld(player.getLocation()) && !hasScore(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> createScoreboard(player), joinDelay);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeScoreboard(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World fromWorld = event.getFrom().getWorld();

        if (event.getTo() == null) return;

        World toWorld = event.getTo().getWorld();

        if (toWorld == null) return;
        if (fromWorld == toWorld) return;

        if (inDisabledWorld(toWorld) && players.containsKey(player.getUniqueId())) {
            removeScoreboard(player);
        } else if (!players.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> createScoreboard(player), worldDelay);
        }
    }
}
