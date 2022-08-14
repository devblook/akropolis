package team.devblook.akropolis.module.modules.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

public class ChatLock extends Module {
    private boolean isChatLocked;

    public ChatLock(AkropolisPlugin plugin) {
        super(plugin, ModuleType.CHAT_LOCK);
    }

    @Override
    public void onEnable() {
        isChatLocked = getPlugin().getConfigManager().getFile(ConfigType.DATA).get().getBoolean("chat_locked");
    }

    @Override
    public void onDisable() {
        getPlugin().getConfigManager().getFile(ConfigType.DATA).get().set("chat_locked", isChatLocked);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!isChatLocked || player.hasPermission(Permissions.LOCK_CHAT_BYPASS.getPermission()))
            return;

        event.setCancelled(true);
        player.sendMessage(Messages.CHAT_LOCKED.toComponent());
    }

    public boolean isChatLocked() {
        return isChatLocked;
    }

    public void setChatLocked(boolean chatLocked) {
        isChatLocked = chatLocked;
    }
}
