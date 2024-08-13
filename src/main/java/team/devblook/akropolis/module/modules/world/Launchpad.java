/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2024 DevBlook Team and others
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

package team.devblook.akropolis.module.modules.world;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.cooldown.CooldownType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

import java.util.List;
import java.util.Objects;

public class Launchpad extends Module {
    private double launch;
    private double launchY;
    private List<String> actions;
    private Material topBlock;
    private Material bottomBlock;

    public Launchpad(AkropolisPlugin plugin) {
        super(plugin, ModuleType.LAUNCHPAD);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        launch = config.getDouble("launchpad.launch_power", 1.3);
        launchY = config.getDouble("launchpad.launch_power_y", 1.2);
        actions = config.getStringList("launchpad.actions");

        if (launch > 4.0)
            launch = 4.0;
        if (launchY > 4.0)
            launchY = 4.0;


        String rawTopBlock = config.getString("launchpad.top_block");
        String rawBottomBlock = config.getString("launchpad.bottom_block");

        if (rawTopBlock == null) {
            getPlugin().getLogger().severe("Launchpad' top block is missing, using air item!");
            XMaterial.matchXMaterial("AIR").ifPresent(m -> topBlock = m.parseMaterial());
        } else {
            XMaterial.matchXMaterial(rawTopBlock).ifPresent(m -> topBlock = m.parseMaterial());
        }

        if (rawBottomBlock == null) {
            getPlugin().getLogger().severe("Launchpad' bottom block is missing, using air item!");
            XMaterial.matchXMaterial("AIR").ifPresent(m -> bottomBlock = m.parseMaterial());
        } else {
            XMaterial.matchXMaterial(rawBottomBlock).ifPresent(m -> bottomBlock = m.parseMaterial());
        }
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onLaunchPadInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || event.getAction() != Action.PHYSICAL)
            return;

        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();
        Location blockLocation = Objects.requireNonNull(event.getClickedBlock()).getLocation();

        if (inDisabledWorld(blockLocation))
            return;

        // Check for launchpad block and cooldown
        if (blockLocation.getBlock().getType() == topBlock && blockLocation.subtract(0, 1, 0).getBlock().getType() == bottomBlock
                && tryCooldown(player.getUniqueId(), CooldownType.LAUNCHPAD, 1)) {
            player.setVelocity(playerLocation.getDirection().multiply(launch).setY(launchY));
            executeActions(player, actions);
        }
    }
}
