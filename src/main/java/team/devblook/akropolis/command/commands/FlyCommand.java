package team.devblook.akropolis.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.ConfigManager;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.util.TextUtil;

import java.util.List;

public class FlyCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;
    private final FileConfiguration dataConfig;
    private final boolean saveState;

    public FlyCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "fly", "Toggle flight mode", "/fly [player]", aliases);
        this.plugin = plugin;

        ConfigManager configManager = plugin.getConfigManager();

        this.dataConfig = configManager.getFile(ConfigType.DATA).get();
        this.saveState = configManager.getFile(ConfigType.SETTINGS).get().getBoolean("fly.save_state", false);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Messages.CONSOLE_NOT_ALLOWED.toComponent());
                return;
            }

            if (!(sender.hasPermission(Permissions.COMMAND_FLIGHT.getPermission()))) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            if (player.getAllowFlight()) {
                player.sendMessage(Messages.FLIGHT_DISABLE.toComponent());
                toggleFlight(player, false);
            } else {
                player.sendMessage(Messages.FLIGHT_ENABLE.toComponent());
                toggleFlight(player, true);
            }
        } else if (args.length == 1) {
            if (!(sender.hasPermission(Permissions.COMMAND_FLIGHT_OTHERS.getPermission()))) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(TextUtil.replace(Messages.INVALID_PLAYER.toComponent(), "player", TextUtil.parse(args[0])));
                return;
            }

            if (player.getAllowFlight()) {
                player.sendMessage(Messages.FLIGHT_DISABLE.toComponent());
                sender.sendMessage(TextUtil.replace(Messages.FLIGHT_DISABLE_OTHER.toComponent(), "player", player.name()));
                toggleFlight(player, false);
            } else {
                player.sendMessage(Messages.FLIGHT_ENABLE.toComponent());
                sender.sendMessage(TextUtil.replace(Messages.FLIGHT_ENABLE_OTHER.toComponent(), "player", player.name()));
                toggleFlight(player, true);
            }
        }

    }

    private void toggleFlight(Player player, boolean value) {
        player.setAllowFlight(value);
        player.setFlying(value);

        if (!saveState) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                dataConfig.set("players." + player.getUniqueId() + ".fly", value));
    }
}
