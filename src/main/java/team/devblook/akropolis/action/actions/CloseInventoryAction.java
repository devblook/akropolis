package team.devblook.akropolis.action.actions;

import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;

public class CloseInventoryAction implements Action {

    @Override
    public String getIdentifier() {
        return "CLOSE";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        player.closeInventory();
    }
}
