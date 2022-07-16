package fun.lewisdev.deluxehub.action.actions;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.Action;
import fun.lewisdev.deluxehub.util.PlaceholderUtil;
import fun.lewisdev.deluxehub.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

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
