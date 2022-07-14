package fun.lewisdev.deluxehub.command.commands.gamemode;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import fun.lewisdev.deluxehub.util.TextUtil;
import net.kyori.adventure.text.Component;
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
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot change gamemode");
                return;
            }

            Player player = (Player) sender;

            if (!player.hasPermission(Permissions.COMMAND_GAMEMODE.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            player.sendMessage(TextUtil.replace(Messages.GAMEMODE_CHANGE.toComponent(), "<gamemode>", TextUtil.parse("SPECTATOR")));
            player.setGameMode(GameMode.SPECTATOR);
        } else if (args.length == 1) {
            if (!sender.hasPermission(Permissions.COMMAND_GAMEMODE_OTHERS.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(TextUtil.replace(Messages.INVALID_PLAYER.toComponent(), "player", Component.text(args[0])));
                return;
            }

            if (sender.getName().equals(player.getName())) {
                player.sendMessage(TextUtil.replace(Messages.GAMEMODE_CHANGE.toComponent(), "<gamemode>", Component.text("SPECTATOR")));
            } else {
                player.sendMessage(TextUtil.replace(Messages.GAMEMODE_CHANGE.toComponent(), "<gamemode>", Component.text("SPECTATOR")));
                sender.sendMessage(TextUtil.replace(TextUtil.replace(Messages.GAMEMODE_CHANGE_OTHER.toComponent(), "player", player.name()), "<gamemode>", Component.text("SPECTATOR")));
            }

            player.setGameMode(GameMode.SPECTATOR);
        }

    }
}