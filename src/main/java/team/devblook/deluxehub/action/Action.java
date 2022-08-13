package team.devblook.deluxehub.action;

import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;

public interface Action {

    String getIdentifier();

    void execute(DeluxeHubPlugin plugin, Player player, String data);
}
