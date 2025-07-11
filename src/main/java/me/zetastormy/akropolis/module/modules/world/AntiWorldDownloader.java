/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2025 DevBlook Team and others
 *
 * Akropolis free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akropolis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akropolis. If not, see <http://www.gnu.org/licenses/>.
 */

package me.zetastormy.akropolis.module.modules.world;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

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

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(0);
        out.writeBoolean(false);

        player.sendPluginMessage(getPlugin(), "wdl:control", out.toByteArray());

        if (!getPlugin().getConfigManager().getFile(ConfigType.SETTINGS).get()
                .getBoolean("anti_wdl.admin_notify"))
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(Permissions.ANTI_WDL_NOTIFY.getPermission())) {
                p.sendMessage(TextUtil.replace(Message.WORLD_DOWNLOAD_NOTIFY.toComponent(), "player", player.name()));
            }
        }
    }
}
