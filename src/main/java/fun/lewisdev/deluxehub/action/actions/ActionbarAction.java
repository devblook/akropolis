package fun.lewisdev.deluxehub.action.actions;

import com.cryptomorin.xseries.messages.ActionBar;
import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.Action;
import org.bukkit.entity.Player;

public class ActionbarAction implements Action {

    @Override
    public String getIdentifier() {
        return "ACTIONBAR";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        ActionBar.sendActionBar(player, plugin.getTextUtil().color(data));
    }
}
