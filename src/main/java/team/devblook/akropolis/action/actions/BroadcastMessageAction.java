package team.devblook.akropolis.action.actions;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;
import team.devblook.akropolis.util.PlaceholderUtil;

public class BroadcastMessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "BROADCAST";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        Component parsedData = PlaceholderUtil.setPlaceholders(data, player);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(parsedData);
        }
    }
}
