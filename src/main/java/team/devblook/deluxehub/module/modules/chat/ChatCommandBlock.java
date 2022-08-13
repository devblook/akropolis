package team.devblook.deluxehub.module.modules.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.Permissions;
import team.devblook.deluxehub.config.ConfigType;
import team.devblook.deluxehub.config.Messages;
import team.devblook.deluxehub.module.Module;
import team.devblook.deluxehub.module.ModuleType;

import java.util.List;

public class ChatCommandBlock extends Module {
    private List<String> blockedCommands;

    public ChatCommandBlock(DeluxeHubPlugin plugin) {
        super(plugin, ModuleType.COMMAND_BLOCK);
    }

    @Override
    public void onEnable() {
        blockedCommands = getConfig(ConfigType.SETTINGS).getStringList("command_block.blocked_commands");
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation())
                || player.hasPermission(Permissions.BLOCKED_COMMANDS_BYPASS.getPermission()))
            return;

        if (blockedCommands.contains(event.getMessage().toLowerCase())) {
            event.setCancelled(true);
            player.sendMessage(Messages.COMMAND_BLOCKED.toComponent());
        }
    }
}
