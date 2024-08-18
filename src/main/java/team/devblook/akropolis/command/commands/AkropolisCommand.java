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

package team.devblook.akropolis.command.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.CommandManager;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.inventory.AbstractInventory;
import team.devblook.akropolis.inventory.InventoryManager;
import team.devblook.akropolis.module.ModuleManager;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.hologram.Hologram;
import team.devblook.akropolis.module.modules.hotbar.HotbarItem;
import team.devblook.akropolis.module.modules.hotbar.HotbarManager;
import team.devblook.akropolis.module.modules.visual.scoreboard.ScoreboardManager;
import team.devblook.akropolis.module.modules.world.LobbySpawn;
import team.devblook.akropolis.util.TextUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AkropolisCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public AkropolisCommand(AkropolisPlugin plugin) {
        super(plugin, "akropolis", "View plugin information and additional commands", Collections.singletonList("akro"));
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        /*
         * Command: help Description: displays help message
         */
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

            if (!sender.hasPermission(Permissions.COMMAND_AKROPOLIS_HELP.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            Message.HELP_PLUGIN.sendFromAsList(sender);
            return;
        }

        /*
         * Command: reload Description: reloads the entire plugin
         */
        else if (args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission(Permissions.COMMAND_AKROPOLIS_RELOAD.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            long start = System.currentTimeMillis();
            plugin.reload();
            Message.CONFIG_RELOAD.sendFromWithReplacement(sender, "time", TextUtil.parse(String.valueOf(System.currentTimeMillis() - start)));
        }

        /*
         * Command: scoreboard Description: toggles the scoreboard on/off
         */
        else if (args[0].equalsIgnoreCase("scoreboard")) {

            if (!(sender instanceof Player player)) {
                Message.CONSOLE_NOT_ALLOWED.sendFrom(sender);
                return;
            }

            if (!sender.hasPermission(Permissions.COMMAND_SCOREBOARD_TOGGLE.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            if (!plugin.getModuleManager().isEnabled(ModuleType.SCOREBOARD)) {
                sender.sendMessage(TextUtil.parse("<red>The scoreboard module is not enabled in the configuration."));
                return;
            }

            ScoreboardManager scoreboardManager = ((ScoreboardManager) plugin.getModuleManager()
                    .getModule(ModuleType.SCOREBOARD));

            if (scoreboardManager.hasScore(player.getUniqueId())) {
                scoreboardManager.removeScoreboard(player);
                Message.SCOREBOARD_DISABLE.sendFrom(player);
            } else {
                scoreboardManager.createScoreboard(player);
                Message.SCOREBOARD_ENABLE.sendFrom(player);
            }
        }

        /*
         * Command: info Description: displays useful information about the
         * configuration
         */
        else if (args[0].equalsIgnoreCase("info")) {

            if (!sender.hasPermission(Permissions.COMMAND_AKROPOLIS_HELP.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            sender.sendMessage(TextUtil.parse("<gold><b>Akropolis <reset><dark_gray>|| <gray>Plugin information<dark_gray>:"));

            Location location = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY)).getLocation();
            sender.sendMessage(
                    TextUtil.parse("<dark_gray>» <gray>Spawn set <dark_gray>- " + (location != null ? "<green>yes" : "<red>no <gray><i>(/setlobby)")));

            ModuleManager moduleManager = plugin.getModuleManager();
            sender.sendMessage(TextUtil.parse("<dark_gray>» <gray>Disabled Worlds (" + moduleManager.getDisabledWorlds().size()
                    + ") <dark_gray>- <green>" + (String.join(", ", moduleManager.getDisabledWorlds()))));

            InventoryManager inventoryManager = plugin.getInventoryManager();
            sender.sendMessage(TextUtil.parse("<dark_gray>» <gray>Custom menus (" + inventoryManager.getInventories().size() + ")"
                    + " <dark_gray>- <green>" + (String.join(", ", inventoryManager.getInventories().keySet()))));

            HotbarManager hotbarManager = ((HotbarManager) plugin.getModuleManager()
                    .getModule(ModuleType.HOTBAR_ITEMS));
            sender.sendMessage(TextUtil
                    .parse("<dark_gray>» <gray>Hotbar items (" + hotbarManager.getHotbarItems().size() + ")" + " <dark_gray>- <green>" + (hotbarManager
                            .getHotbarItems().stream().map(HotbarItem::getKeyValue).collect(Collectors.joining(", ")))));

            CommandManager commandManager = plugin.getCommandManager();
            sender.sendMessage(TextUtil.parse("<dark_gray>» <gray>Custom commands (" + commandManager.getCustomCommands().size() + ")"
                    + " <dark_gray>- <green>" + (commandManager.getCustomCommands().stream()
                    .map(command -> command.getAliases().getFirst()).collect(Collectors.joining(", ")))));

            sender.sendMessage(TextUtil.parse("<dark_gray>» <gray>PlaceholderAPI hook<dark_gray>: "
                    + (plugin.getHookManager().isHookEnabled("PLACEHOLDER_API") ? "<green>yes" : "<red>no")));
            sender.sendMessage(TextUtil.parse("<dark_gray>» <gray>HeadDatabase hook<dark_gray>: "
                    + (plugin.getHookManager().isHookEnabled("HEAD_DATABASE") ? "<green>yes" : "<red>no")));
            sender.sendMessage(TextUtil.parse("<dark_gray>» <gray>MiniPlaceholders hook<dark_gray>: "
                    + (plugin.getHookManager().isHookEnabled("MINIPLACEHOLDERS") ? "<green>yes" : "<red>no")));
        }

        /*
         * Command: open Description: opens a custom menu
         */
        else if (args[0].equalsIgnoreCase("open")) {
            if (!(sender instanceof Player)) {
                Message.CONSOLE_NOT_ALLOWED.sendFrom(sender);
                return;
            }

            if (!sender.hasPermission(Permissions.COMMAND_OPEN_MENUS.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            if (args.length == 1) {
                Message.USAGE.sendFromWithReplacement(sender, "command", Component.text("akropolis open <menu>"));
                return;
            }

            AbstractInventory inventory = plugin.getInventoryManager().getInventory(args[1]);
            if (inventory == null) {
                sender.sendMessage(TextUtil.parse("<red>" + args[1] + " is not a valid menu ID."));
                return;
            }
            inventory.openInventory((Player) sender);
        }

        /*
         * Holograms
         */
        if (args[0].equalsIgnoreCase("hologram") || args[0].equalsIgnoreCase("holo")) {

            if (!(sender instanceof Player player)) {
                Message.CONSOLE_NOT_ALLOWED.sendFrom(sender);
                return;
            }

            if (!sender.hasPermission(Permissions.COMMAND_HOLOGRAMS.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            if (args.length == 1) {
                Message.HELP_HOLOGRAM.toComponentList().forEach(sender::sendMessage);
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {

                if (plugin.getHologramManager().getHolograms().isEmpty()) {
                    Message.HOLOGRAMS_EMPTY.sendFrom(sender);
                    return;
                }

                sender.sendMessage(TextUtil.parse("<gold><b>Akropolis <reset><dark_gray>|| <gray>Hologram list<dark_gray>:"));
                for (Hologram entry : plugin.getHologramManager().getHolograms()) {
                    sender.sendMessage(TextUtil.parse("<dark_gray>- <gray>" + entry.getName()));
                }
            }

            if (args[1].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    Message.USAGE.sendFromWithReplacement(sender, "command", Component.text("akropolis hologram create <id>"));
                    return;
                }

                if (plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_ALREADY_EXISTS.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().createHologram(args[2], player.getLocation());
                List<Component> defaultMsg = new ArrayList<>();
                defaultMsg.add(TextUtil.parse("<gray>Created new Hologram called <aqua>" + args[2]));
                defaultMsg.add(TextUtil.parse("<gray>Use <aqua>/akropolis holo <gray>to customise"));
                holo.setLines(defaultMsg);
                Message.HOLOGRAMS_SPAWNED.sendFromWithReplacement(sender, "name", TextUtil.parse(args[2]));
                return;
            }

            if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete")) {
                if (args.length == 2) {
                    Message.USAGE.sendFromWithReplacement(sender, "command", Component.text("akropolis hologram remove <id>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                plugin.getHologramManager().deleteHologram(args[2]);
                Message.HOLOGRAMS_DESPAWNED.sendFromWithReplacement(sender, "name", TextUtil.parse(args[2]));
                return;
            }

            if (args[1].equalsIgnoreCase("setline")) {
                if (args.length < 5) {
                    Message.USAGE.sendFromWithReplacement(sender, "command", Component.text("akropolis hologram setline <id> <line> <text>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                int line = Integer.parseInt(args[3]);
                String text = TextUtil.joinString(4, args);

                if (holo.hasInvalidLine(line)) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_INVALID_LINE.toComponent(), "line", TextUtil.parse(String.valueOf(line))));
                    return;
                }

                holo.setLine(line, text);
                Message.HOLOGRAMS_LINE_SET.sendFromWithReplacement(sender, "line", TextUtil.parse(String.valueOf(line)));
                return;
            }

            if (args[1].equalsIgnoreCase("addline")) {
                if (args.length <= 3) {
                    Message.USAGE.sendFromWithReplacement(sender, "command", Component.text("akropolis hologram addline <id> <text>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                Component text = TextUtil.parse(TextUtil.joinString(3, args));

                holo.addLine(text);
                Message.HOLOGRAMS_ADDED_LINE.sendFromWithReplacement(sender, "name", TextUtil.parse(args[2]));
            }

            if (args[1].equalsIgnoreCase("removeline")) {
                if (args.length != 4) {
                    Message.USAGE.sendFromWithReplacement(sender, "command", Component.text("akropolis hologram removeline <id> <line>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                int line = Integer.parseInt(args[3]);

                if (holo.hasInvalidLine(line)) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_INVALID_LINE.toComponent(), "line", TextUtil.parse(String.valueOf(line))));
                    return;
                }

                if (holo.removeLine(line) == null) {
                    plugin.getHologramManager().deleteHologram(args[2]);
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_REMOVED_LINE.toComponent(), "name", TextUtil.parse(args[2])));
                }

                return;
            }

            if (args[1].equalsIgnoreCase("move")) {
                if (args.length == 2) {
                    Message.USAGE.sendFromWithReplacement(sender, "command", Component.text("akropolis hologram move <id>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Message.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);

                holo.setLocation(player.getLocation());
                Message.HOLOGRAMS_MOVED.sendFromWithReplacement(sender, "name", TextUtil.parse(args[2]));
            }
        }

    }
}
