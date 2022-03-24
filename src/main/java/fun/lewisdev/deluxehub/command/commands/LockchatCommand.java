package fun.lewisdev.deluxehub.command.commands;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import fun.lewisdev.deluxehub.module.ModuleType;
import fun.lewisdev.deluxehub.module.modules.chat.ChatLock;
import org.bukkit.command.CommandSender;

import java.util.List;

public class LockchatCommand extends InjectableCommand {
    private final DeluxeHubPlugin plugin;

    public LockchatCommand(DeluxeHubPlugin plugin, List<String> aliases) {
        super(plugin, "lockchat", "Locks global chat", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_LOCKCHAT.getPermission())) {
            sender.sendMessage(Messages.NO_PERMISSION.toString());
            return;
        }

        ChatLock chatLockModule = (ChatLock) plugin.getModuleManager().getModule(ModuleType.CHAT_LOCK);

        if (chatLockModule.isChatLocked()) {
            plugin.getServer().broadcastMessage(
                    Messages.CHAT_UNLOCKED_BROADCAST.toString().replace("%player%", sender.getName()));
            chatLockModule.setChatLocked(false);
        } else {
            plugin.getServer()
                    .broadcastMessage(Messages.CHAT_LOCKED_BROADCAST.toString().replace("%player%", sender.getName()));
            chatLockModule.setChatLocked(true);
        }

    }
}
