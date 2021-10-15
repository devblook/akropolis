package fun.lewisdev.deluxehub.module.modules.world;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.config.ConfigType;
import fun.lewisdev.deluxehub.cooldown.CooldownType;
import fun.lewisdev.deluxehub.module.Module;
import fun.lewisdev.deluxehub.module.ModuleType;
import fun.lewisdev.deluxehub.utility.universal.XMaterial;

public class Launchpad extends Module {
    private double launch;
    private double launchY;
    private List<String> actions;
    private Material topBlock;
    private Material bottomBlock;

    public Launchpad(DeluxeHubPlugin plugin) {
        super(plugin, ModuleType.LAUNCHPAD);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        launch = config.getDouble("launchpad.launch_power", 1.3);
        launchY = config.getDouble("launchpad.launch_power_y", 1.2);
        actions = config.getStringList("launchpad.actions");

        XMaterial.matchXMaterial(config.getString("launchpad.top_block")).ifPresent(m -> topBlock = m.parseMaterial());
        XMaterial.matchXMaterial(config.getString("launchpad.bottom_block"))
                .ifPresent(m -> bottomBlock = m.parseMaterial());

        if (launch > 4.0)
            launch = 4.0;
        if (launchY > 4.0)
            launchY = 4.0;
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onPlayerMove(PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) return;
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if (inDisabledWorld(location))
            return;

        // Check for launchpad block and cooldown
        if (event.getMaterial() == topBlock && location.subtract(0, 1, 0).getBlock().getType() == bottomBlock
                && tryCooldown(player.getUniqueId(), CooldownType.LAUNCHPAD, 1)) {
            player.setVelocity(location.getDirection().multiply(launch).setY(launchY));
            executeActions(player, actions);
        }
    }
}
