package team.devblook.deluxehub.action.actions;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;
import team.devblook.deluxehub.util.PlaceholderUtil;
import team.devblook.deluxehub.util.TextUtil;

public class BroadcastMessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "BROADCAST";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        Component parsedData = PlaceholderUtil.setPlaceholders(data, player);

        if (data.contains("<center>") && data.contains("</center>"))
            parsedData = TextUtil.getCenteredMessage(parsedData);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(parsedData);
        }
    }
}
