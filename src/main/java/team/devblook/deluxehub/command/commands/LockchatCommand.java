package team.devblook.deluxehub.command.commands;

import org.bukkit.command.CommandSender;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.Permissions;
import team.devblook.deluxehub.command.InjectableCommand;
import team.devblook.deluxehub.config.Messages;
import team.devblook.deluxehub.module.ModuleType;
import team.devblook.deluxehub.module.modules.chat.ChatLock;
import team.devblook.deluxehub.util.TextUtil;

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
