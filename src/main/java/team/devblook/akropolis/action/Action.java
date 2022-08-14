package team.devblook.akropolis.action;

import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;

public interface Action {

    String getIdentifier();

    void execute(AkropolisPlugin plugin, Player player, String data);
}
