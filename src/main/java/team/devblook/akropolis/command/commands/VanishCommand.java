package team.devblook.akropolis.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.player.PlayerVanish;

import java.util.List;

public class VanishCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public VanishCommand(AkropolisPlugin plugin, List<String> aliases) {
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
