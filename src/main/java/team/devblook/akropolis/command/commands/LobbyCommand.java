package team.devblook.akropolis.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.world.LobbySpawn;
import team.devblook.akropolis.util.TextUtil;

import java.util.List;

public class LobbyCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public LobbyCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "lobby", "Teleport to the lobby (if set)", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot teleport to spawn");
            return;
        }

        Location location = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY)).getLocation();
        if (location == null) {
            sender.sendMessage(TextUtil.parse("<red>The spawn location has not been set <gray>(/setlobby)<red>."));
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> ((Player) sender).teleport(location), 3L);

    }
}
