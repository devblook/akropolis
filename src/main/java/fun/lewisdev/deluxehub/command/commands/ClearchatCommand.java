package fun.lewisdev.deluxehub.command.commands;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import fun.lewisdev.deluxehub.util.TextUtil;
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
            sender.sendMessage(Messages.NO_PERMISSION.toComponent());
            return;
        }

        if (args.length == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < 100; i++) {
                    player.sendMessage("");
                }

                player.sendMessage(TextUtil.replace(Messages.CLEARCHAT.toComponent(), "player", sender.name()));
            }
        } else if (args.length == 1) {

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(TextUtil.replace(Messages.INVALID_PLAYER.toComponent(), "player", TextUtil.parse(args[0])));
                return;
            }

            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }

            sender.sendMessage(TextUtil.replace(Messages.CLEARCHAT_PLAYER.toComponent(), "player", sender.name()));
        }

    }
}
