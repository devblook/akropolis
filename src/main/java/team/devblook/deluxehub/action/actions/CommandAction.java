package team.devblook.deluxehub.action.actions;

import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;

public class CommandAction implements Action {

    @Override
    public String getIdentifier() {
        return "COMMAND";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        player.chat(data.contains("/") ? data : "/" + data);
    }
}
