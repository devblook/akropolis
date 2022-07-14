package fun.lewisdev.deluxehub.command.commands;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import fun.lewisdev.deluxehub.module.ModuleType;
import fun.lewisdev.deluxehub.module.modules.chat.ChatLock;
import fun.lewisdev.deluxehub.util.TextUtil;
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
            sender.sendMessage(Messages.NO_PERMISSION.toComponent());
            return;
        }

        ChatLock chatLockModule = (ChatLock) plugin.getModuleManager().getModule(ModuleType.CHAT_LOCK);

        if (chatLockModule.isChatLocked()) {
            plugin.getServer().broadcast(
                    TextUtil.replace(Messages.CHAT_UNLOCKED_BROADCAST.toComponent(), "player", sender.name()));
            chatLockModule.setChatLocked(false);
        } else {
            plugin.getServer()
                    .broadcast(
                            TextUtil.replace(Messages.CHAT_LOCKED_BROADCAST.toComponent(), "player", sender.name()));
            chatLockModule.setChatLocked(true);
        }

    }
}
