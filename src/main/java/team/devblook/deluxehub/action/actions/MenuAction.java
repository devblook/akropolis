package team.devblook.deluxehub.action.actions;

import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;
import team.devblook.deluxehub.inventory.AbstractInventory;

import java.util.logging.Level;

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
