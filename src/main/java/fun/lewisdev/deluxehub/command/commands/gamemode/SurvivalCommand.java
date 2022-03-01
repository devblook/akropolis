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

public class SurvivalCommand extends InjectableCommand {

    public SurvivalCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "gms", "Change to survival mode", "/gms [player]", aliases);
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

            player.sendMessage(Messages.GAMEMODE_CHANGE.toString().replace("%gamemode%", "SURVIVAL"));
            player.setGameMode(GameMode.SURVIVAL);
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

            player.sendMessage(Messages.GAMEMODE_CHANGE.toString().replace("%gamemode%", "SURVIVAL"));
            sender.sendMessage(Messages.GAMEMODE_CHANGE_OTHER.toString().replace("%player%", player.getName())
                    .replace("%gamemode%", "SURVIVAL"));
            player.setGameMode(GameMode.SURVIVAL);
        }

        return true;
    }
}
