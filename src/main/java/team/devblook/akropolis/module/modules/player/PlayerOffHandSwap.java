package team.devblook.akropolis.module.modules.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

public class PlayerOffHandSwap extends Module {

    public PlayerOffHandSwap(AkropolisPlugin plugin) {
        super(plugin, ModuleType.PLAYER_OFFHAND_LISTENER);
    }

    @Override
    public void onEnable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getRawSlot() != event.getSlot() && event.getCursor() != null && event.getSlot() == 40) {
            event.setCancelled(true);
        }
    }
}
