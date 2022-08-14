package team.devblook.akropolis.action.actions;

import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;

public class CommandAction implements Action {

    @Override
    public String getIdentifier() {
        return "COMMAND";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        player.chat(data.contains("/") ? data : "/" + data);
    }
}
