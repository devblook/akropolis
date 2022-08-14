package team.devblook.akropolis.action.actions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;

public class ServerAction implements Action {

    @Override
    public String getIdentifier() {
        return "SERVER";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF(data);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
