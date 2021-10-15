package fun.lewisdev.deluxehub.action.actions;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.Action;
import fun.lewisdev.deluxehub.inventory.AbstractInventory;

public class MenuAction implements Action {

    @Override
    public String getIdentifier() {
        return "MENU";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        AbstractInventory inventory = plugin.getInventoryManager().getInventory(data);

        if (inventory != null) {
            inventory.openInventory(player);
        } else {
            plugin.getLogger().log(Level.WARNING, "[MENU] Action Failed: Menu {0} not found.", data);
        }
    }
}
