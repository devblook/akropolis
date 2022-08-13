package team.devblook.deluxehub.action.actions;

import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;

public class CloseInventoryAction implements Action {

    @Override
    public String getIdentifier() {
        return "CLOSE";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        player.closeInventory();
    }
}
