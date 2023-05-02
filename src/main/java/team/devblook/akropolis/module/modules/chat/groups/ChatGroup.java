package team.devblook.akropolis.module.modules.chat.groups;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import team.devblook.akropolis.util.PlaceholderUtil;
import team.devblook.akropolis.util.TextUtil;

public class ChatGroup {
    private final String rawFormat;
    private final int cooldownTime;
    private final String cooldownMessage;
    private final String permission;

    public ChatGroup(String groupName, String rawFormat, int cooldownTime, String cooldownMessage) {
        this.rawFormat = rawFormat;
        this.cooldownTime = cooldownTime;
        this.cooldownMessage = cooldownMessage;
        this.permission = "akropolis.chat.group." + groupName;
    }

    public Component getFormat(Player player) {
        return PlaceholderUtil.setPlaceholders(rawFormat, player);
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    public Component getCooldownMessage() {
        return TextUtil.parse(cooldownMessage);
    }

    public String getPermission() {
        return permission;
    }
}
