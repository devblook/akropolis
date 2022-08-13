package team.devblook.deluxehub.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.Permissions;
import team.devblook.deluxehub.command.InjectableCommand;
import team.devblook.deluxehub.config.Messages;
import team.devblook.deluxehub.module.ModuleType;
import team.devblook.deluxehub.module.modules.player.PlayerVanish;

import java.util.List;

public class VanishCommand extends InjectableCommand {
    private final DeluxeHubPlugin plugin;

    public VanishCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "vanish", "Disappear into thin air!", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_VANISH.getPermission())) {
            sender.sendMessage(Messages.NO_PERMISSION.toComponent());
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot set the spawn location.");
            return;
        }

        Player player = (Player) sender;
        PlayerVanish vanishModule = ((PlayerVanish) plugin.getModuleManager().getModule(ModuleType.VANISH));
        vanishModule.toggleVanish(player);

    }
}
