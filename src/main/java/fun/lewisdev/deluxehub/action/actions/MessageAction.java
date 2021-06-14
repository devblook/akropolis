package fun.lewisdev.deluxehub.action.actions;

import org.bukkit.entity.Player;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.Action;
import fun.lewisdev.deluxehub.utility.TextUtil;

public class MessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "MESSAGE";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        if (data.contains("<center>") && data.contains("</center>"))
            data = TextUtil.getCenteredMessage(data);
        player.sendMessage(TextUtil.color(data));
    }
}
