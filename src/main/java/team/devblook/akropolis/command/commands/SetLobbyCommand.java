package team.devblook.akropolis.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.world.LobbySpawn;
import team.devblook.akropolis.util.TextUtil;

import java.util.List;

public class SetLobbyCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public SetLobbyCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "setlobby", "Set the lobby location", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_SET_LOBBY.getPermission())) {
            sender.sendMessage(Messages.NO_PERMISSION.toComponent());
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot set the spawn location.");
            return;
        }

        Player player = (Player) sender;

        if (plugin.getModuleManager().getDisabledWorlds().contains(player.getWorld().getName())) {
            sender.sendMessage(TextUtil.parse("<red>You cannot set the lobby location in a disabled world."));
            return;
        }

        LobbySpawn lobbyModule = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY));
        lobbyModule.setLocation(player.getLocation());
        sender.sendMessage(Messages.SET_LOBBY.toComponent());

    }
}
