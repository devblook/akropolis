package team.devblook.deluxehub.command.commands.gamemode;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.Permissions;
import team.devblook.deluxehub.command.InjectableCommand;
import team.devblook.deluxehub.config.Messages;
import team.devblook.deluxehub.util.TextUtil;

import java.util.List;

public class SurvivalCommand extends InjectableCommand {

    public SurvivalCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "gms", "Change to survival mode", "/gms [player]", aliases);
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

            player.sendMessage(TextUtil.replace(Messages.GAMEMODE_CHANGE.toComponent(), "gamemode", TextUtil.parse("SURVIVAL")));
            player.setGameMode(GameMode.SURVIVAL);
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

            player.sendMessage(TextUtil.replace(Messages.GAMEMODE_CHANGE.toComponent(), "gamemode", Component.text("SURVIVAL")));
            sender.sendMessage(TextUtil.replace(TextUtil.replace(Messages.GAMEMODE_CHANGE_OTHER.toComponent(), "player", player.name()), "gamemode", Component.text("SURVIVAL")));
            player.setGameMode(GameMode.SURVIVAL);
        }

    }
}
