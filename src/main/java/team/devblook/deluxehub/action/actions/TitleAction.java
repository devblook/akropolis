package team.devblook.deluxehub.action.actions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;
import team.devblook.deluxehub.util.PlaceholderUtil;

import java.time.Duration;

public class TitleAction implements Action {

    @Override
    public String getIdentifier() {
        return "TITLE";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        String[] args = data.split(";");

        Component title = PlaceholderUtil.setPlaceholders(args[0], player);
        Component subTitle = PlaceholderUtil.setPlaceholders(args[1], player);

        Duration fadeIn;
        Duration stay;
        Duration fadeOut;

        try {
            fadeIn = Duration.ofSeconds(Long.parseLong(args[2]));
            stay = Duration.ofSeconds(Long.parseLong(args[3]));
            fadeOut = Duration.ofSeconds(Long.parseLong(args[4]));
        } catch (NumberFormatException ex) {
            fadeIn = Duration.ofSeconds(1);
            stay = Duration.ofSeconds(3);
            fadeOut = Duration.ofSeconds(1);
        }

        player.showTitle(Title.title(title, subTitle, Title.Times.times(fadeIn, stay, fadeOut)));
    }
}
