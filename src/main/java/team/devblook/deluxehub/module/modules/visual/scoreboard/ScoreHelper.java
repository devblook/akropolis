package team.devblook.deluxehub.module.modules.visual.scoreboard;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.util.PlaceholderUtil;

import java.util.List;

public class ScoreHelper {
    private final Sidebar sidebar;
    private final Player player;

    public ScoreHelper(Player player) {
        this.player = player;
        this.sidebar = DeluxeHubPlugin.getInstance().getScoreboardManager().sidebar(Sidebar.MAX_LINES);
    }

    public void setTitle(String title) {
        sidebar.title(setPlaceholders(title));
    }

    public void setSlotsFromList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            sidebar.line(i, setPlaceholders(list.get(i)));
        }
    }

    public Component setPlaceholders(String text) {
        return PlaceholderUtil.setPlaceholders(text, player);
    }

    public void addPlayer() {
        sidebar.addPlayer(player);
    }

    public void removePlayer() {
        sidebar.removePlayer(player);
    }


    public void visible(boolean visible) {
        sidebar.visible(visible);
    }
}
