package team.devblook.deluxehub.module.modules.visual.tablist;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.util.TextUtil;

import java.util.Objects;

public class TablistHelper {

    private TablistHelper() {
        throw new UnsupportedOperationException();
    }

    public static void sendTabList(Player player, Component header, Component footer) {
        Objects.requireNonNull(player, "Cannot update tab for null player");
        Component newHeader = header == null ? Component.empty() : TextUtil.replace(header, "player", player.displayName());
        Component newFooter = footer == null ? Component.empty() : TextUtil.replace(footer, "player", player.displayName());

        player.sendPlayerListHeaderAndFooter(newHeader, newFooter);
    }
}
