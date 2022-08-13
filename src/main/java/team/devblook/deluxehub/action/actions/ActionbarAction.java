package team.devblook.deluxehub.action.actions;

import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;
import team.devblook.deluxehub.util.PlaceholderUtil;

public class ActionbarAction implements Action {

    @Override
    public String getIdentifier() {
        return "ACTIONBAR";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        player.sendActionBar(PlaceholderUtil.setPlaceholders(data, player));
    }
}
