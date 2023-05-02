package team.devblook.akropolis.module.modules.chat.groups;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.cooldown.CooldownType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class ChatGroups extends Module {
    private final Map<String, ChatGroup> chatGroups = new HashMap<>();

    public ChatGroups(AkropolisPlugin plugin) {
        super(plugin, ModuleType.CHAT_FORMAT);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        ConfigurationSection groups = config.getConfigurationSection("groups");

        if (groups == null) {
            throw new NullPointerException("No chat groups in configuration, please add the new section to you config.yml!");
        }

        groups.getKeys(false).forEach(groupName -> chatGroups.put(groupName, new ChatGroup(groupName,
                groups.getString(groupName + ".format", "No format."),
                groups.getInt(groupName + ".cooldown.time", 0),
                groups.getString(groupName + ".cooldown.message", "No cooldown message."))));
    }

    @Override
    public void onDisable() {
        chatGroups.clear();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ChatGroup currentGroup = chatGroups.get("default");

        for (String group : chatGroups.keySet()) {
            if (player.hasPermission("akropolis.chat.group." + group)) {
                currentGroup = chatGroups.get(group);
            }
        }

        event.setCancelled(true);

        if (!tryCooldown(player.getUniqueId(), CooldownType.CHAT, currentGroup.getCooldownTime())) {
            player.sendMessage(TextUtil.replace(currentGroup.getCooldownMessage(), "time", Component.text(getCooldown(player.getUniqueId(), CooldownType.CHAT))));
            return;
        }

        getPlugin().getServer().sendMessage(TextUtil.replace(currentGroup.getFormat(player),
                "message",
                event.originalMessage()));
    }
}
