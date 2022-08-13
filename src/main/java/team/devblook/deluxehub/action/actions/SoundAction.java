package team.devblook.deluxehub.action.actions;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.action.Action;

import java.util.Optional;

public class SoundAction implements Action {

    @Override
    public String getIdentifier() {
        return "SOUND";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        Optional<XSound> xsound = XSound.matchXSound(data);

        try {
            xsound.ifPresent(s -> {
                Sound sound = s.parseSound();

                if (sound == null) throw new IllegalStateException();

                player.playSound(player.getLocation(), sound, 1L, 1L);
            });
        } catch (Exception ex) {
            plugin.getLogger().warning("[DeluxeHub Action] Invalid sound name: " + data.toUpperCase());
        }
    }
}
