package fun.lewisdev.deluxehub.command.commands;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import fun.lewisdev.deluxehub.module.ModuleType;
import fun.lewisdev.deluxehub.module.modules.player.PlayerVanish;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VanishCommand extends InjectableCommand {
    private final DeluxeHubPlugin plugin;

    public VanishCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "vanish", "Disappear into thin air!", aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_VANISH.getPermission())) {
            sender.sendMessage(Messages.NO_PERMISSION.toString());
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot set the spawn location.");
            return true;
        }

        Player player = (Player) sender;
        PlayerVanish vanishModule = ((PlayerVanish) plugin.getModuleManager().getModule(ModuleType.VANISH));
        vanishModule.toggleVanish(player);

        return true;
    }
}
