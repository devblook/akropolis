package fun.lewisdev.deluxehub.command.commands;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import fun.lewisdev.deluxehub.module.ModuleType;
import fun.lewisdev.deluxehub.module.modules.world.LobbySpawn;
import fun.lewisdev.deluxehub.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetLobbyCommand extends InjectableCommand {
    private final DeluxeHubPlugin plugin;

    public SetLobbyCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "setlobby", "Set the lobby location", aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_SET_LOBBY.getPermission())) {
            sender.sendMessage(Messages.NO_PERMISSION.toString());
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot set the spawn location.");
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getModuleManager().getDisabledWorlds().contains(player.getWorld().getName())) {
            sender.sendMessage(TextUtil.color("&cYou cannot set the lobby location in a disabled world."));
            return true;
        }

        LobbySpawn lobbyModule = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY));
        lobbyModule.setLocation(player.getLocation());
        sender.sendMessage(Messages.SET_LOBBY.toString());

        return true;
    }
}
