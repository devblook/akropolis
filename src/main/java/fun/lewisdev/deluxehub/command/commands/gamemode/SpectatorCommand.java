package fun.lewisdev.deluxehub.command.commands.gamemode;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpectatorCommand extends InjectableCommand {

    public SpectatorCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "gmsp", "Change to spectator mode", "/gmsp [player]", aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot change gamemode");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission(Permissions.COMMAND_GAMEMODE.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return true;
            }

            player.sendMessage(Messages.GAMEMODE_CHANGE.toString().replace("%gamemode%", "SPECTATOR"));
            player.setGameMode(GameMode.SPECTATOR);
        } else if (args.length == 1) {
            if (!sender.hasPermission(Permissions.COMMAND_GAMEMODE_OTHERS.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return true;
            }

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(Messages.INVALID_PLAYER.toString().replace("%player%", args[0]));
                return true;
            }

            if (sender.getName().equals(player.getName())) {
                player.sendMessage(Messages.GAMEMODE_CHANGE.toString().replace("%gamemode%", "SPECTATOR"));
            } else {
                player.sendMessage(Messages.GAMEMODE_CHANGE.toString().replace("%gamemode%", "SPECTATOR"));
                sender.sendMessage(Messages.GAMEMODE_CHANGE_OTHER.toString().replace("%player%", player.getName())
                        .replace("%gamemode%", "SPECTATOR"));
            }

            player.setGameMode(GameMode.SPECTATOR);
        }

        return true;
    }
}