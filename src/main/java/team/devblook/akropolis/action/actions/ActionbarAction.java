package team.devblook.akropolis.action.actions;

import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;
import team.devblook.akropolis.util.PlaceholderUtil;

public class ActionbarAction implements Action {

    @Override
    public String getIdentifier() {
        return "ACTIONBAR";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        player.sendActionBar(PlaceholderUtil.setPlaceholders(data, player));
    }
}
