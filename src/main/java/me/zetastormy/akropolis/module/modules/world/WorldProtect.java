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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.cryptomorin.xseries.XMaterial;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.hologram.Hologram;
import me.zetastormy.akropolis.module.modules.player.FightModeManager;
import net.kyori.adventure.text.Component;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class WorldProtect extends Module {
    private boolean hungerLoss;
    private boolean fallDamage;
    private boolean weatherChange;
    private boolean deathMessage;
    private boolean fireSpread;
    private boolean leafDecay;
    private boolean mobSpawning;
    private boolean blockBurn;
    private boolean voidDeath;
    private boolean itemDrop;
    private boolean itemPickup;
    private boolean blockBreak;
    private boolean blockPlace;
    private boolean blockInteract;
    private boolean playerPvP;
    private boolean playerDrowning;
    private boolean fireDamage;
    private boolean inventoryDrop;

    private static final Set<Material> INTERACTABLE;

    static {
        List<Material> rawInteractable = Arrays.asList(XMaterial.ANVIL.get(), XMaterial.BLACK_BED.get(),
                XMaterial.BLUE_BED.get(), XMaterial.BROWN_BED.get(), XMaterial.CYAN_BED.get(), XMaterial.GRAY_BED.get(),
                XMaterial.GREEN_BED.get(), XMaterial.LIGHT_BLUE_BED.get(), XMaterial.LIME_BED.get(),
                XMaterial.MAGENTA_BED.get(), XMaterial.ORANGE_BED.get(), XMaterial.PINK_BED.get(),
                XMaterial.PURPLE_BED.get(), XMaterial.RED_BED.get(), XMaterial.WHITE_BED.get(),
                XMaterial.YELLOW_BED.get(), XMaterial.BELL.get(), XMaterial.BLAST_FURNACE.get(),
                XMaterial.BREWING_STAND.get(), XMaterial.ACACIA_BUTTON.get(), XMaterial.BAMBOO_BUTTON.get(),
                XMaterial.BIRCH_BUTTON.get(), XMaterial.CHERRY_BUTTON.get(), XMaterial.CRIMSON_BUTTON.get(),
                XMaterial.DARK_OAK_BUTTON.get(), XMaterial.JUNGLE_BUTTON.get(), XMaterial.MANGROVE_BUTTON.get(),
                XMaterial.OAK_BUTTON.get(), XMaterial.POLISHED_BLACKSTONE_BUTTON.get(), XMaterial.SPRUCE_BUTTON.get(),
                XMaterial.STONE_BUTTON.get(), XMaterial.WARPED_BUTTON.get(), XMaterial.PALE_OAK_BUTTON.get(),
                XMaterial.CARTOGRAPHY_TABLE.get(), XMaterial.CAULDRON.get(), XMaterial.CHEST.get(),
                XMaterial.TRAPPED_CHEST.get(), XMaterial.DAYLIGHT_DETECTOR.get(), XMaterial.CHEST_MINECART.get(),
                XMaterial.COMMAND_BLOCK_MINECART.get(), XMaterial.FURNACE_MINECART.get(),
                XMaterial.HOPPER_MINECART.get(), XMaterial.TNT_MINECART.get(), XMaterial.COMMAND_BLOCK.get(),
                XMaterial.COMPOSTER.get(), XMaterial.CRAFTING_TABLE.get(), XMaterial.ACACIA_DOOR.get(),
                XMaterial.BAMBOO_DOOR.get(), XMaterial.BIRCH_DOOR.get(), XMaterial.CHERRY_DOOR.get(),
                XMaterial.COPPER_DOOR.get(), XMaterial.CRIMSON_DOOR.get(), XMaterial.DARK_OAK_DOOR.get(),
                XMaterial.EXPOSED_COPPER_DOOR.get(), XMaterial.IRON_DOOR.get(), XMaterial.JUNGLE_DOOR.get(),
                XMaterial.MANGROVE_DOOR.get(), XMaterial.OAK_DOOR.get(), XMaterial.OXIDIZED_COPPER_DOOR.get(),
                XMaterial.SPRUCE_DOOR.get(), XMaterial.WARPED_DOOR.get(), XMaterial.WAXED_COPPER_DOOR.get(),
                XMaterial.WAXED_EXPOSED_COPPER_DOOR.get(), XMaterial.WAXED_OXIDIZED_COPPER_DOOR.get(),
                XMaterial.WAXED_WEATHERED_COPPER_DOOR.get(), XMaterial.WEATHERED_COPPER_DOOR.get(),
                XMaterial.PALE_OAK_DOOR.get(), XMaterial.ENCHANTING_TABLE.get(), XMaterial.END_PORTAL_FRAME.get(),
                XMaterial.ACACIA_FENCE_GATE.get(), XMaterial.BAMBOO_FENCE_GATE.get(),
                XMaterial.BIRCH_FENCE_GATE.get(), XMaterial.CHERRY_FENCE_GATE.get(),
                XMaterial.CRIMSON_FENCE_GATE.get(), XMaterial.DARK_OAK_FENCE_GATE.get(),
                XMaterial.JUNGLE_FENCE_GATE.get(), XMaterial.MANGROVE_FENCE_GATE.get(),
                XMaterial.OAK_FENCE_GATE.get(), XMaterial.SPRUCE_FENCE_GATE.get(),
                XMaterial.WARPED_FENCE_GATE.get(), XMaterial.PALE_OAK_FENCE_GATE.get(),
                XMaterial.ACACIA_CHEST_BOAT.get(), XMaterial.BAMBOO_CHEST_RAFT.get(),
                XMaterial.BIRCH_CHEST_BOAT.get(), XMaterial.CHERRY_CHEST_BOAT.get(),
                XMaterial.DARK_OAK_CHEST_BOAT.get(), XMaterial.JUNGLE_CHEST_BOAT.get(),
                XMaterial.MANGROVE_CHEST_BOAT.get(), XMaterial.OAK_CHEST_BOAT.get(),
                XMaterial.SPRUCE_CHEST_BOAT.get(), XMaterial.PALE_OAK_CHEST_BOAT.get(), XMaterial.GRINDSTONE.get(),
                XMaterial.ITEM_FRAME.get(), XMaterial.JUKEBOX.get(), XMaterial.LECTERN.get(), XMaterial.LEVER.get(),
                XMaterial.LODESTONE.get(), XMaterial.LOOM.get(), XMaterial.NOTE_BLOCK.get(),
                XMaterial.ACACIA_PRESSURE_PLATE.get(), XMaterial.BAMBOO_PRESSURE_PLATE.get(),
                XMaterial.BIRCH_PRESSURE_PLATE.get(), XMaterial.CHERRY_PRESSURE_PLATE.get(),
                XMaterial.CRIMSON_PRESSURE_PLATE.get(), XMaterial.DARK_OAK_PRESSURE_PLATE.get(),
                XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.get(), XMaterial.JUNGLE_PRESSURE_PLATE.get(),
                XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.get(), XMaterial.MANGROVE_PRESSURE_PLATE.get(),
                XMaterial.OAK_PRESSURE_PLATE.get(), XMaterial.POLISHED_BLACKSTONE_PRESSURE_PLATE.get(),
                XMaterial.SPRUCE_PRESSURE_PLATE.get(), XMaterial.STONE_PRESSURE_PLATE.get(),
                XMaterial.WARPED_PRESSURE_PLATE.get(), XMaterial.PALE_OAK_PRESSURE_PLATE.get(),
                XMaterial.PUMPKIN.get(), XMaterial.RESPAWN_ANCHOR.get(), XMaterial.SMITHING_TABLE.get(),
                XMaterial.SMOKER.get(), XMaterial.STONECUTTER.get(), XMaterial.TNT.get(),
                XMaterial.ACACIA_TRAPDOOR.get(), XMaterial.BAMBOO_TRAPDOOR.get(), XMaterial.BIRCH_TRAPDOOR.get(),
                XMaterial.CHERRY_TRAPDOOR.get(), XMaterial.COPPER_TRAPDOOR.get(), XMaterial.CRIMSON_TRAPDOOR.get(),
                XMaterial.DARK_OAK_TRAPDOOR.get(), XMaterial.EXPOSED_COPPER_TRAPDOOR.get(),
                XMaterial.IRON_TRAPDOOR.get(), XMaterial.JUNGLE_TRAPDOOR.get(), XMaterial.MANGROVE_TRAPDOOR.get(),
                XMaterial.OAK_TRAPDOOR.get(), XMaterial.OXIDIZED_COPPER_TRAPDOOR.get(), XMaterial.SPRUCE_TRAPDOOR.get(),
                XMaterial.WARPED_TRAPDOOR.get(), XMaterial.WAXED_COPPER_TRAPDOOR.get(),
                XMaterial.WAXED_EXPOSED_COPPER_TRAPDOOR.get(), XMaterial.WAXED_OXIDIZED_COPPER_TRAPDOOR.get(),
                XMaterial.WAXED_WEATHERED_COPPER_TRAPDOOR.get(), XMaterial.WEATHERED_COPPER_TRAPDOOR.get(),
                XMaterial.PALE_OAK_TRAPDOOR.get(), XMaterial.FLOWER_POT.get(), XMaterial.PAINTING.get(),
                XMaterial.BEACON.get(), XMaterial.DISPENSER.get(), XMaterial.HOPPER.get(), XMaterial.DROPPER.get(),
                XMaterial.ENDER_CHEST.get(), XMaterial.COMPARATOR.get(), XMaterial.ACACIA_SIGN.get(),
                XMaterial.ACACIA_WALL_HANGING_SIGN.get(), XMaterial.ACACIA_WALL_SIGN.get(),
                XMaterial.BAMBOO_HANGING_SIGN.get(), XMaterial.BAMBOO_SIGN.get(),
                XMaterial.BAMBOO_WALL_HANGING_SIGN.get(), XMaterial.BAMBOO_WALL_SIGN.get(),
                XMaterial.BIRCH_HANGING_SIGN.get(), XMaterial.BIRCH_SIGN.get(), XMaterial.BIRCH_WALL_HANGING_SIGN.get(),
                XMaterial.BIRCH_WALL_SIGN.get(), XMaterial.CHERRY_HANGING_SIGN.get(),
                XMaterial.CHERRY_SIGN.get(), XMaterial.CHERRY_WALL_HANGING_SIGN.get(),
                XMaterial.CHERRY_WALL_SIGN.get(), XMaterial.CRIMSON_HANGING_SIGN.get(),
                XMaterial.CRIMSON_SIGN.get(), XMaterial.CRIMSON_WALL_HANGING_SIGN.get(),
                XMaterial.CRIMSON_WALL_SIGN.get(), XMaterial.DARK_OAK_HANGING_SIGN.get(),
                XMaterial.DARK_OAK_SIGN.get(), XMaterial.DARK_OAK_WALL_HANGING_SIGN.get(),
                XMaterial.DARK_OAK_WALL_SIGN.get(), XMaterial.JUNGLE_HANGING_SIGN.get(),
                XMaterial.JUNGLE_SIGN.get(), XMaterial.JUNGLE_WALL_HANGING_SIGN.get(),
                XMaterial.JUNGLE_WALL_SIGN.get(), XMaterial.MANGROVE_HANGING_SIGN.get(),
                XMaterial.MANGROVE_SIGN.get(), XMaterial.MANGROVE_WALL_HANGING_SIGN.get(),
                XMaterial.MANGROVE_WALL_SIGN.get(), XMaterial.OAK_HANGING_SIGN.get(),
                XMaterial.OAK_SIGN.get(), XMaterial.OAK_WALL_HANGING_SIGN.get(),
                XMaterial.OAK_WALL_SIGN.get(), XMaterial.SPRUCE_HANGING_SIGN.get(),
                XMaterial.SPRUCE_SIGN.get(), XMaterial.SPRUCE_WALL_HANGING_SIGN.get(),
                XMaterial.SPRUCE_WALL_SIGN.get(), XMaterial.WARPED_HANGING_SIGN.get(),
                XMaterial.WARPED_SIGN.get(), XMaterial.WARPED_WALL_HANGING_SIGN.get(),
                XMaterial.WARPED_WALL_SIGN.get(), XMaterial.PALE_OAK_HANGING_SIGN.get(),
                XMaterial.PALE_OAK_SIGN.get(), XMaterial.PALE_OAK_WALL_HANGING_SIGN.get(),
                XMaterial.PALE_OAK_WALL_SIGN.get());

        INTERACTABLE = rawInteractable.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }

    public WorldProtect(AkropolisPlugin plugin) {
        super(plugin, ModuleType.WORLD_PROTECT);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        hungerLoss = config.getBoolean("world_settings.disable_hunger_loss");
        fallDamage = config.getBoolean("world_settings.disable_fall_damage");
        playerPvP = config.getBoolean("world_settings.disable_player_pvp");
        voidDeath = config.getBoolean("world_settings.disable_void_death");
        weatherChange = config.getBoolean("world_settings.disable_weather_change");
        deathMessage = config.getBoolean("world_settings.disable_death_message");
        mobSpawning = config.getBoolean("world_settings.disable_mob_spawning");
        itemDrop = config.getBoolean("world_settings.disable_item_drop");
        itemPickup = config.getBoolean("world_settings.disable_item_pickup");
        blockBreak = config.getBoolean("world_settings.disable_block_break");
        blockPlace = config.getBoolean("world_settings.disable_block_place");
        blockInteract = config.getBoolean("world_settings.disable_block_interact");
        blockBurn = config.getBoolean("world_settings.disable_block_burn");
        fireSpread = config.getBoolean("world_settings.disable_block_fire_spread");
        leafDecay = config.getBoolean("world_settings.disable_block_leaf_decay");
        playerDrowning = config.getBoolean("world_settings.disable_drowning");
        fireDamage = config.getBoolean("world_settings.disable_fire_damage");
        inventoryDrop = config.getBoolean("world_settings.disable_inventory_drop", true);
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {
        for (Hologram entry : getPlugin().getHologramManager().getHolograms()) {
            for (ArmorStand stand : entry.getStands()) {
                if (stand.equals(event.getRightClicked())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!blockBreak || event.isCancelled())
            return;

        Player player = event.getPlayer();
        if (inDisabledWorld(player.getLocation()))
            return;
        if (player.hasPermission(Permissions.EVENT_BLOCK_BREAK.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), "block_break", 3)) {
            Component message = Message.EVENT_BLOCK_BREAK.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler
    public void onBlockPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (!blockPlace || event.isCancelled())
            return;

        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        ItemStack item = event.getItem();

        if (item == null) return;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        if (container.has(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            return;
        }

        if (player.hasPermission(Permissions.EVENT_BLOCK_PLACE.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(event.getPlayer().getUniqueId(), "block_place", 3)) {
            Component message = Message.EVENT_BLOCK_PLACE.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!blockBurn)
            return;

        if (inDisabledWorld(event.getBlock().getLocation()))
            return;

        event.setCancelled(true);
    }

    // Prevent destroying of item frame/paintings
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDestroy(HangingBreakByEntityEvent event) {
        if (!blockBreak || inDisabledWorld(event.getEntity().getLocation()))
            return;

        Entity entity = event.getEntity();
        Entity player = event.getRemover();

        if (entity instanceof Painting || entity instanceof ItemFrame && player instanceof Player) {
            if (player.hasPermission(Permissions.EVENT_BLOCK_BREAK.getPermission()))
                return;

            event.setCancelled(true);

            if (tryCooldown(player.getUniqueId(), "block_break", 3)) {
                Component message = Message.EVENT_BLOCK_BREAK.toComponent();

                if (message != Component.empty()) player.sendMessage(message);
            }
        }
    }

    // Prevent items being rotated in item frame
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!blockInteract || inDisabledWorld(event.getRightClicked().getLocation()))
            return;

        Entity entity = event.getRightClicked();
        Entity player = event.getPlayer();

        if (player.hasPermission(Permissions.EVENT_BLOCK_INTERACT.getPermission()))
            return;

        if (entity instanceof ItemFrame) {
            event.setCancelled(true);

            if (tryCooldown(player.getUniqueId(), "block_interact", 3)) {
                Component message = Message.EVENT_BLOCK_INTERACT.toComponent();

                if (message != Component.empty()) player.sendMessage(message);
            }
        }
    }

    // Prevent items being taken from item frames
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!blockInteract || inDisabledWorld(event.getEntity().getLocation()))
            return;

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (entity instanceof ItemFrame && damager instanceof Player player) {

            if (player.hasPermission(Permissions.EVENT_BLOCK_INTERACT.getPermission()))
                return;

            event.setCancelled(true);

            if (tryCooldown(player.getUniqueId(), "block_interact", 3)) {
                Component message = Message.EVENT_BLOCK_INTERACT.toComponent();

                if (message != Component.empty()) player.sendMessage(message);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!blockInteract || inDisabledWorld(event.getPlayer().getLocation()))
            return;

        Player player = event.getPlayer();

        if (player.hasPermission(Permissions.EVENT_BLOCK_INTERACT.getPermission()))
            return;

        Block block = event.getClickedBlock();

        if (block == null)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (INTERACTABLE.contains(block.getType()) || block.getType().toString().contains("POTTED")) {
                event.setCancelled(true);

                if (tryCooldown(player.getUniqueId(), "block_interact", 3)) {
                    Component message = Message.EVENT_BLOCK_INTERACT.toComponent();
                    if (message != Component.empty()) player.sendMessage(message);
                }
            }
        } else if (event.getAction() == Action.PHYSICAL && block.getType() == XMaterial.FARMLAND.get()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (inDisabledWorld(player.getLocation()))
            return;

        if (fallDamage && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        } else if (playerDrowning && event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            event.setCancelled(true);
        } else if (fireDamage && (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            event.setCancelled(true);
        } else if (voidDeath && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            player.setFallDistance(0.0F);

            Location location = ((LobbySpawn) getPlugin().getModuleManager().getModule(ModuleType.LOBBY)).getLocation();

            if (location == null)
                return;

            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> player.teleportAsync(location), 3L);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireSpread(BlockIgniteEvent event) {
        if (!fireSpread)
            return;

        if (inDisabledWorld(event.getBlock().getLocation()))
            return;

        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!hungerLoss)
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (inDisabledWorld(player.getLocation()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropEvent(PlayerDropItemEvent event) {
        if (!itemDrop)
            return;

        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        if (player.hasPermission(Permissions.EVENT_ITEM_DROP.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), "item_drop", 3)) {
            Component message = Message.EVENT_ITEM_DROP.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupEvent(PlayerPickupItemEvent event) {
        if (!itemPickup)
            return;

        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        if (player.hasPermission(Permissions.EVENT_ITEM_PICKUP.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), "item_pickup", 3)) {
            Component message = Message.EVENT_ITEM_PICKUP.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeafDecay(LeavesDecayEvent event) {
        if (!leafDecay)
            return;

        if (inDisabledWorld(event.getBlock().getLocation()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!mobSpawning)
            return;

        if (inDisabledWorld(event.getEntity().getLocation()))
            return;

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (inDisabledWorld(event.getWorld()))
            return;

        if (!weatherChange)
            return;

        event.setCancelled(event.toWeatherState());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (inDisabledWorld(event.getEntity().getLocation()))
            return;

        if (deathMessage)
            event.setDeathMessage(null);

        if (!inventoryDrop)
            return;

        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!playerPvP)
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (inDisabledWorld(player.getLocation()))
            return;

        FightModeManager fightModeManager = getPlugin().getFightModeManager();
        boolean attackedInFightMode = fightModeManager.isInFightMode(player.getUniqueId());
        boolean attackerInFightMode = fightModeManager.isInFightMode(event.getDamager().getUniqueId());

        if (attackedInFightMode && attackerInFightMode) {
            return;
        }

        if (event.getDamager().hasPermission(Permissions.EVENT_PLAYER_PVP.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), "player_pvp", 3)) {
            Component message = Message.EVENT_PLAYER_PVP.toComponent();

            if (message != Component.empty()) event.getDamager().sendMessage(message);
        }
    }
}
