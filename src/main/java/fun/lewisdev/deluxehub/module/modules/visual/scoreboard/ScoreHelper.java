package fun.lewisdev.deluxehub.module.modules.visual.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.util.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreHelper {
    private final FastBoard fastBoard;
    private final Player player;

    public ScoreHelper(Player player) {
        this.player = player;
        this.fastBoard = new FastBoard(player);
    }

    public void setTitle(String title) {
        fastBoard.updateTitle(setPlaceholders(title));
    }

    public void setSlotsFromList(List<String> list) {
        fastBoard.updateLines(setPlaceholders(list));
    }

    public List<String> setPlaceholders(List<String> textList) {
        List<String> parsedList = new ArrayList<>();

        for (String text : textList) {
            parsedList.add(setPlaceholders(text));
        }

        return parsedList;
    }

    public String setPlaceholders(String text) {
        //TODO: Find a way to not make this class dependant on TextUtil.
        return DeluxeHubPlugin.getPlugin(DeluxeHubPlugin.class).getTextUtil().color(PlaceholderUtil.setPlaceholders(text, player));
    }
}
