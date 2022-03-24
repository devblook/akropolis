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

public class GamemodeCommand extends InjectableCommand {

    public GamemodeCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "gamemode", "Allows you to change gamemode", "/gamemode <gamemode> [player]", aliases);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot change gamemode");
                return;
            }

            Player player = (Player) sender;

            if (!player.hasPermission(Permissions.COMMAND_GAMEMODE.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return;
            }

            GameMode gamemode = getGamemode(args[0]);

            if (gamemode == null) {
                sender.sendMessage(Messages.GAMEMODE_INVALID.toString().replace("%gamemode%", args[0]));
                return;
            }

            player.sendMessage(
                    Messages.GAMEMODE_CHANGE.toString().replace("%gamemode%", gamemode.toString().toUpperCase()));
            player.setGameMode(gamemode);

        } else if (args.length == 2) {
            if (!sender.hasPermission(Permissions.COMMAND_GAMEMODE_OTHERS.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(Messages.INVALID_PLAYER.toString().replace("%player%", args[0]));
                return;
            }

            GameMode gamemode = getGamemode(args[0]);

            if (gamemode == null) {
                sender.sendMessage(Messages.GAMEMODE_INVALID.toString().replace("%gamemode%", args[0]));
                return;
            }

            String gamemodeChange = Messages.GAMEMODE_CHANGE.toString().replace("%gamemode%", gamemode.toString().toUpperCase());

            if (sender.getName().equals(player.getName())) {
                player.sendMessage(gamemodeChange);
            } else {
                player.sendMessage(gamemodeChange);
                sender.sendMessage(Messages.GAMEMODE_CHANGE_OTHER.toString().replace("%player%", player.getName())
                        .replace("%gamemode%", gamemode.toString().toUpperCase()));
            }

            player.setGameMode(gamemode);
        }

    }

    private GameMode getGamemode(String gamemode) {
        if (gamemode.equals("0") || gamemode.equalsIgnoreCase("survival") || gamemode.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        } else if (gamemode.equals("1") || gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        } else if (gamemode.equals("2") || gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        } else if (gamemode.equals("3") || gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp")) {
            return GameMode.SPECTATOR;
        }

        return null;
    }
}
