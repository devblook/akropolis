package fun.lewisdev.deluxehub.action;

import org.bukkit.entity.Player;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;

public interface Action {

    String getIdentifier();

    void execute(DeluxeHubPlugin plugin, Player player, String data);
}
