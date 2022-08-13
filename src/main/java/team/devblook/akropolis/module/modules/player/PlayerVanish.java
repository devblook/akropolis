package team.devblook.akropolis.module.modules.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerVanish extends Module {
    private List<UUID> vanished;

    public PlayerVanish(AkropolisPlugin plugin) {
        super(plugin, ModuleType.VANISH);
    }

    @Override
    public void onEnable() {
        vanished = new ArrayList<>();
    }

    @Override
    public void onDisable() {
        vanished.clear();
    }

    @SuppressWarnings("deprecation")
    public void toggleVanish(Player player) {
        if (isVanished(player)) {
            vanished.remove(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> pl.showPlayer(player));

            player.sendMessage(Messages.VANISH_DISABLE.toComponent());
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        } else {
            vanished.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> pl.hidePlayer(player));

            player.sendMessage(Messages.VANISH_ENABLE.toComponent());
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1));
        }
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        vanished.forEach(hidden -> {
            Player playerHidden = Bukkit.getPlayer(hidden);

            if (playerHidden == null) return;

            event.getPlayer().hidePlayer(playerHidden);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        vanished.remove(player.getUniqueId());
    }
}
