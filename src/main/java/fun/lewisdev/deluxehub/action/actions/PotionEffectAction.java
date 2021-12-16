package fun.lewisdev.deluxehub.action.actions;

import com.cryptomorin.xseries.XPotion;
import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.Action;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;

public class PotionEffectAction implements Action {

    @Override
    public String getIdentifier() {
        return "EFFECT";
    }

    @Override
    public void execute(DeluxeHubPlugin plugin, Player player, String data) {
        String[] args = data.split(";");
        Optional<XPotion> xpotion = XPotion.matchXPotion(args[0]);

        try {
            xpotion.ifPresent(p -> {
                PotionEffect potionEffect = p.parsePotion(1000000, Integer.parseInt(args[1]) - 1);

                if (potionEffect == null) throw new IllegalStateException();

                player.addPotionEffect(potionEffect);
            });
        } catch (Exception e) {
            plugin.getLogger().warning("[DeluxeHub Action] Invalid potion effect name: " + data.toUpperCase());
        }
    }
}
