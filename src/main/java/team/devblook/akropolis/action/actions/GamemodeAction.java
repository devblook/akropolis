package team.devblook.akropolis.action.actions;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;

public class GamemodeAction implements Action {

    @Override
    public String getIdentifier() {
        return "GAMEMODE";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        try {
            player.setGameMode(GameMode.valueOf(data.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().warning("[Akropolis Action] Invalid gamemode name: " + data.toUpperCase());
        }
    }
}
