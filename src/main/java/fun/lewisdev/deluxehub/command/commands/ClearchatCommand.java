package fun.lewisdev.deluxehub.command.commands;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearchatCommand extends InjectableCommand {

    public ClearchatCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "clearchat", "Clear global or a player's chat", "/clearchat [player]", aliases);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender.hasPermission(Permissions.COMMAND_CLEARCHAT.getPermission()))) {
            sender.sendMessage(Messages.NO_PERMISSION.toString());
            return;
        }

        if (args.length == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < 100; i++) {
                    player.sendMessage("");
                }

                player.sendMessage(Messages.CLEARCHAT.toString().replace("%player%", sender.getName()));
            }
        } else if (args.length == 1) {

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(Messages.INVALID_PLAYER.toString().replace("%player%", args[0]));
                return;
            }

            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }

            sender.sendMessage(Messages.CLEARCHAT_PLAYER.toString().replace("%player%", sender.getName()));
        }

    }
}
