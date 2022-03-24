package fun.lewisdev.deluxehub.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("NullableProblems")
public abstract class InjectableCommand extends Command implements PluginIdentifiableCommand {
    private final Plugin plugin;

    protected InjectableCommand(Plugin plugin, String name, String description, List<String> aliases) {
        super(name, description, "/" + name, aliases);
        this.plugin = plugin;
    }

    protected InjectableCommand(Plugin plugin, String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            onCommand(sender, label, args);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred processing this command. Please make sure your parameters are correct.");
            e.printStackTrace();
        }

        sender.sendMessage(this.getUsage());
        return true;
    }

    protected abstract void onCommand(CommandSender sender, String label, String[] args);
}
