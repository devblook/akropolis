package team.devblook.akropolis.action.actions;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;
import team.devblook.akropolis.util.PlaceholderUtil;
import team.devblook.akropolis.util.TextUtil;

public class MessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "MESSAGE";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        Component parsedData = PlaceholderUtil.setPlaceholders(data, player);

        if (data.contains("<center>") && data.contains("</center>"))
            parsedData = TextUtil.getCenteredMessage(parsedData);

        player.sendMessage(parsedData);
    }
}
