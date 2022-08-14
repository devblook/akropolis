package team.devblook.akropolis.module.modules.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

import java.util.List;

public class ChatCommandBlock extends Module {
    private List<String> blockedCommands;

    public ChatCommandBlock(AkropolisPlugin plugin) {
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
