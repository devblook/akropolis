package team.devblook.akropolis.module.modules.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

public class ChatFormat extends Module {

    public ChatFormat(AkropolisPlugin plugin) {
        super(plugin, ModuleType.CHAT_FORMAT);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {

    }
}
