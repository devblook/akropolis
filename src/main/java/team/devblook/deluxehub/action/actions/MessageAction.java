package team.devblook.deluxehub.action.actions;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;
import team.devblook.deluxehub.util.PlaceholderUtil;
import team.devblook.deluxehub.util.TextUtil;

public class MessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "MESSAGE";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        Component parsedData = PlaceholderUtil.setPlaceholders(data, player);

        if (data.contains("<center>") && data.contains("</center>"))
            parsedData = TextUtil.getCenteredMessage(parsedData);

        player.sendMessage(parsedData);
    }
}
