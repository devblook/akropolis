package team.devblook.akropolis.module.modules.world;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.TextUtil;

public class AntiWorldDownloader extends Module implements PluginMessageListener {

    public AntiWorldDownloader(AkropolisPlugin plugin) {
        super(plugin, ModuleType.ANTI_WDL);
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getMessenger().registerIncomingPluginChannel(getPlugin(), "wdl:init", this);
        getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(getPlugin(), "wdl:control");
    }

    @Override
    public void onDisable() {
        getPlugin().getServer().getMessenger().unregisterIncomingPluginChannel(getPlugin(), "wdl:init");
        getPlugin().getServer().getMessenger().unregisterOutgoingPluginChannel(getPlugin(), "wdl:control");
    }

    @SuppressWarnings("NullableProblems")
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if (player.hasPermission(Permissions.ANTI_WDL_BYPASS.getPermission()))
            return;

        if (!channel.equals("wdl:init")) return;

        @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(0);
        out.writeBoolean(false);

        player.sendPluginMessage(getPlugin(), "wdl:control", out.toByteArray());

        if (!getPlugin().getConfigManager().getFile(ConfigType.SETTINGS).get()
                .getBoolean("anti_wdl.admin_notify"))
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(Permissions.ANTI_WDL_NOTIFY.getPermission())) {
                p.sendMessage(TextUtil.replace(Messages.WORLD_DOWNLOAD_NOTIFY.toComponent(), "player", player.name()));
            }
        }
    }
}
