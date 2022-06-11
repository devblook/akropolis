package fun.lewisdev.deluxehub.action.actions;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.Action;
import fun.lewisdev.deluxehub.util.TextUtil;
import org.bukkit.entity.Player;

public class MessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "MESSAGE";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        TextUtil textUtil = plugin.getTextUtil();

        if (data.contains("<center>") && data.contains("</center>"))
            data = textUtil.getCenteredMessage(data);

        player.sendMessage(textUtil.color(data));
    }
}
