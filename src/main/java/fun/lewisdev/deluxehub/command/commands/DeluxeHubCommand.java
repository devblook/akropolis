package fun.lewisdev.deluxehub.command.commands;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.Permissions;
import fun.lewisdev.deluxehub.command.CommandManager;
import fun.lewisdev.deluxehub.command.InjectableCommand;
import fun.lewisdev.deluxehub.config.Messages;
import fun.lewisdev.deluxehub.inventory.AbstractInventory;
import fun.lewisdev.deluxehub.inventory.InventoryManager;
import fun.lewisdev.deluxehub.module.ModuleManager;
import fun.lewisdev.deluxehub.module.ModuleType;
import fun.lewisdev.deluxehub.module.modules.hologram.Hologram;
import fun.lewisdev.deluxehub.module.modules.hotbar.HotbarItem;
import fun.lewisdev.deluxehub.module.modules.hotbar.HotbarManager;
import fun.lewisdev.deluxehub.module.modules.visual.scoreboard.ScoreboardManager;
import fun.lewisdev.deluxehub.module.modules.world.LobbySpawn;
import fun.lewisdev.deluxehub.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeluxeHubCommand extends InjectableCommand {
    private final DeluxeHubPlugin plugin;

    public DeluxeHubCommand(DeluxeHubPlugin plugin) {
        super(plugin, "deluxehub", "View plugin information and additional commands", Collections.singletonList("dhub"));
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        PluginDescriptionFile pdfFile = plugin.getDescription();

        /*
         * Command: help Description: displays help message
         */
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

            if (!sender.hasPermission(Permissions.COMMAND_DELUXEHUB_HELP.getPermission())) {
                sender.sendMessage(TextUtil.color(
                        "&8&l> &7Server is running &dDeluxeHub &ev" + pdfFile.getVersion() + " &7By &6ItsLewizzz"));
                return true;
            }

            sender.sendMessage("");
            sender.sendMessage(TextUtil.color("&d&lDeluxeHub " + "&fv" + plugin.getDescription().getVersion()));
            sender.sendMessage(TextUtil.color("&7Author: &fItsLewizzz"));
            sender.sendMessage("");
            sender.sendMessage(
                    TextUtil.color(" &d/deluxehub info &8- &7&oDisplays information about the current config"));
            sender.sendMessage(TextUtil.color(" &d/deluxehub scoreboard &8- &7&oToggle the scoreboard"));
            sender.sendMessage(TextUtil.color(" &d/deluxehub open <menu> &8- &7&oOpen a custom menu"));
            sender.sendMessage(TextUtil.color(" &d/deluxehub hologram &8- &7&oView the hologram help"));
            sender.sendMessage("");
            sender.sendMessage(TextUtil.color("  &d/vanish &8- &7&oToggle vanish mode"));
            sender.sendMessage(TextUtil.color("  &d/fly &8- &7&oToggle flight mode"));
            sender.sendMessage(TextUtil.color("  &d/setlobby &8- &7&oSet the spawn location"));
            sender.sendMessage(TextUtil.color("  &d/lobby &8- &7&oTeleport to the spawn location"));
            sender.sendMessage(TextUtil.color("  &d/gamemode <gamemode> &8- &7&oSet your gamemode"));
            sender.sendMessage(TextUtil.color("  &d/gmc &8- &7&oGo into creative mode"));
            sender.sendMessage(TextUtil.color("  &d/gms &8- &7&oGo into survival mode"));
            sender.sendMessage(TextUtil.color("  &d/gma &8- &7&oGo into adventure mode"));
            sender.sendMessage(TextUtil.color("  &d/gmsp &8- &7&oGo into spectator mode"));
            sender.sendMessage(TextUtil.color("  &d/clearchat &8- &7&oClear global chat"));
            sender.sendMessage(TextUtil.color("  &d/lockchat &8- &7&oLock/unlock global chat"));
            sender.sendMessage("");
            return true;
        }

        /*
         * Command: reload Description: reloads the entire plugin
         */
        else if (args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission(Permissions.COMMAND_DELUXEHUB_RELOAD.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return true;
            }

            long start = System.currentTimeMillis();
            plugin.reload();
            sender.sendMessage(Messages.CONFIG_RELOAD.toString().replace("%time%",
                    String.valueOf(System.currentTimeMillis() - start)));
            int inventories = plugin.getInventoryManager().getInventories().size();
            if (inventories > 0) {
                sender.sendMessage(TextUtil.color("&8- &7Loaded &a" + inventories + "&7 custom menus."));
            }
        }

        /*
         * Command: scoreboard Description: toggles the scoreboard on/off
         */
        else if (args[0].equalsIgnoreCase("scoreboard")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot toggle the scoreboard");
                return true;
            }

            if (!sender.hasPermission(Permissions.COMMAND_SCOREBOARD_TOGGLE.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return true;
            }

            if (!plugin.getModuleManager().isEnabled(ModuleType.SCOREBOARD)) {
                sender.sendMessage(TextUtil.color("&cThe scoreboard module is not enabled in the configuration."));
                return true;
            }

            Player player = (Player) sender;
            ScoreboardManager scoreboardManager = ((ScoreboardManager) plugin.getModuleManager()
                    .getModule(ModuleType.SCOREBOARD));

            if (scoreboardManager.hasScore(player.getUniqueId())) {
                scoreboardManager.removeScoreboard(player);
                player.sendMessage(Messages.SCOREBOARD_TOGGLE.toString().replace("%value%", "disabled"));
            } else {
                scoreboardManager.createScoreboard(player);
                player.sendMessage(Messages.SCOREBOARD_TOGGLE.toString().replace("%value%", "enabled"));
            }
        }

        /*
         * Command: info Description: displays useful information about the
         * configuration
         */
        else if (args[0].equalsIgnoreCase("info")) {

            if (!sender.hasPermission(Permissions.COMMAND_DELUXEHUB_HELP.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return true;
            }

            sender.sendMessage("");
            sender.sendMessage(TextUtil.color("&d&lPlugin Information"));
            sender.sendMessage("");

            Location location = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY)).getLocation();
            sender.sendMessage(
                    TextUtil.color("&7Spawn set &8- " + (location != null ? "&ayes" : "&cno &7&o(/setlobby)")));

            sender.sendMessage("");

            ModuleManager moduleManager = plugin.getModuleManager();
            sender.sendMessage(TextUtil.color("&7Disabled Worlds (" + moduleManager.getDisabledWorlds().size()
                    + ") &8- &a" + (String.join(", ", moduleManager.getDisabledWorlds()))));

            InventoryManager inventoryManager = plugin.getInventoryManager();
            sender.sendMessage(TextUtil.color("&7Custom menus (" + inventoryManager.getInventories().size() + ")"
                    + " &8- &a" + (String.join(", ", inventoryManager.getInventories().keySet()))));

            HotbarManager hotbarManager = ((HotbarManager) plugin.getModuleManager()
                    .getModule(ModuleType.HOTBAR_ITEMS));
            sender.sendMessage(TextUtil
                    .color("&7Hotbar items (" + hotbarManager.getHotbarItems().size() + ")" + " &8- &a" + (hotbarManager
                            .getHotbarItems().stream().map(HotbarItem::getKey).collect(Collectors.joining(", ")))));

            CommandManager commandManager = plugin.getCommandManager();
            sender.sendMessage(TextUtil.color("&7Custom commands (" + commandManager.getCustomCommands().size() + ")"
                    + " &8- &a" + (commandManager.getCustomCommands().stream()
                    .map(command -> command.getAliases().get(0)).collect(Collectors.joining(", ")))));

            sender.sendMessage("");

            sender.sendMessage(TextUtil.color("&7PlaceholderAPI Hook: "
                    + (plugin.getHookManager().isHookEnabled("PLACEHOLDER_API") ? "&ayes" : "&cno")));
            sender.sendMessage(TextUtil.color("&7HeadDatabase Hook: "
                    + (plugin.getHookManager().isHookEnabled("HEAD_DATABASE") ? "&ayes" : "&cno")));

            sender.sendMessage("");
        }

        /*
         * Command: open Description: opens a custom menu
         */
        else if (args[0].equalsIgnoreCase("open")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot open menus");
                return true;
            }

            if (!sender.hasPermission(Permissions.COMMAND_OPEN_MENUS.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return true;
            }

            if (args.length == 1) {
                sender.sendMessage(TextUtil.color("&cUsage: /deluxehub open <menu>"));
                return true;
            }

            AbstractInventory inventory = plugin.getInventoryManager().getInventory(args[1]);
            if (inventory == null) {
                sender.sendMessage(TextUtil.color("&c" + args[1] + " is not a valid menu ID."));
                return true;
            }
            inventory.openInventory((Player) sender);
        }

        /*
         * Holograms
         */
        if (args[0].equalsIgnoreCase("hologram") || args[0].equalsIgnoreCase("holo")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("You cannot do this command.");
                return true;
            }

            if (!sender.hasPermission(Permissions.COMMAND_HOLOGRAMS.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toString());
                return true;
            }

            if (args.length == 1) {
                sender.sendMessage("");
                sender.sendMessage(TextUtil.color("&d&lDeluxeHub Holograms"));
                sender.sendMessage("");
                sender.sendMessage(TextUtil.color(" &d/" + label + " hologram list"));
                sender.sendMessage(TextUtil.color("   &7&oList all created holograms"));
                sender.sendMessage(TextUtil.color(" &d/" + label + " hologram create <id>"));
                sender.sendMessage(TextUtil.color("   &7&oCreate a new hologram"));
                sender.sendMessage(TextUtil.color(" &d/" + label + " hologram remove <id>"));
                sender.sendMessage(TextUtil.color("   &7&oDelete an existing hologram"));
                sender.sendMessage(TextUtil.color(" &d/" + label + " hologram move <id>"));
                sender.sendMessage(TextUtil.color("   &7&oMove the location of a hologram"));
                sender.sendMessage(TextUtil.color(""));
                sender.sendMessage(TextUtil.color(" &d/" + label + " hologram setline <id> <line> <text>"));
                sender.sendMessage(TextUtil.color("   &7&oSet the line of a specific hologram"));
                sender.sendMessage(TextUtil.color(" &d/" + label + " hologram addline <id> <text>"));
                sender.sendMessage(TextUtil.color("   &7&oAdd a new line to a hologram"));
                sender.sendMessage(TextUtil.color(" &d/" + label + " hologram removeline <id> <line>"));
                sender.sendMessage(TextUtil.color("   &7&oRemove a line from a hologram"));
                sender.sendMessage("");
                return true;
            }

            Player player = (Player) sender;

            if (args[1].equalsIgnoreCase("list")) {

                if (plugin.getHologramManager().getHolograms().isEmpty()) {
                    sender.sendMessage(Messages.HOLOGRAMS_EMPTY.toString());
                    return true;
                }

                sender.sendMessage("");
                sender.sendMessage(TextUtil.color("&d&lHologram List"));
                for (Hologram entry : plugin.getHologramManager().getHolograms()) {
                    sender.sendMessage(TextUtil.color("&8- &7" + entry.getName()));
                }
                sender.sendMessage("");
            }

            if (args[1].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    sender.sendMessage(TextUtil.color("&cUsage: /deluxehub hologram create <id>"));
                    return true;
                }

                if (plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_ALREADY_EXISTS.toString().replace("%name%", args[2]));
                    return true;
                }

                Hologram holo = plugin.getHologramManager().createHologram(args[2], player.getLocation());
                List<String> defaultMsg = new ArrayList<>();
                defaultMsg.add("&7Created new Hologram called &b" + args[2]);
                defaultMsg.add("&7Use &b/deluxehub holo &7to customise");
                holo.setLines(defaultMsg);
                sender.sendMessage(Messages.HOLOGRAMS_SPAWNED.toString().replace("%name%", args[2]));
                return true;
            }

            if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete")) {
                if (args.length == 2) {
                    sender.sendMessage(TextUtil.color("&cUsage: /deluxehub hologram remove <id>"));
                    return true;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_INVALID_HOLOGRAM.toString().replace("%name%", args[2]));
                    return true;
                }

                plugin.getHologramManager().deleteHologram(args[2]);
                sender.sendMessage(Messages.HOLOGRAMS_DESPAWNED.toString().replace("%name%", args[2]));
                return true;
            }

            if (args[1].equalsIgnoreCase("setline")) {
                if (args.length < 5) {
                    sender.sendMessage(TextUtil.color("&cUsage: /deluxehub hologram setline <id> <line> <text>"));
                    return true;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_INVALID_HOLOGRAM.toString().replace("%name%", args[2]));
                    return true;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                int line = Integer.parseInt(args[3]);
                String text = TextUtil.joinString(4, args);

                if (holo.hasInvalidLine(line)) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_INVALID_LINE.toString().replace("%line%", String.valueOf(line)));
                    return true;
                }

                holo.setLine(line, text);
                sender.sendMessage(Messages.HOLOGRAMS_LINE_SET.toString().replace("%line%", String.valueOf(line)));
                return true;
            }

            if (args[1].equalsIgnoreCase("addline")) {
                if (args.length <= 3) {
                    sender.sendMessage(TextUtil.color("&cUsage: /deluxehub hologram addline <id> <text>"));
                    return true;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_INVALID_HOLOGRAM.toString().replace("%name%", args[2]));
                    return true;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                String text = TextUtil.joinString(3, args);

                holo.addLine(text);
                sender.sendMessage(Messages.HOLOGRAMS_ADDED_LINE.toString().replace("%name%", args[2]));
            }

            if (args[1].equalsIgnoreCase("removeline")) {
                if (args.length != 4) {
                    sender.sendMessage(TextUtil.color("&cUsage: /deluxehub hologram removeline <id> <line>"));
                    return true;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_INVALID_HOLOGRAM.toString().replace("%name%", args[2]));
                    return true;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                int line = Integer.parseInt(args[3]);

                if (holo.hasInvalidLine(line)) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_INVALID_LINE.toString().replace("%line%", String.valueOf(line)));
                    return true;
                }

                if (holo.removeLine(line) == null) {
                    plugin.getHologramManager().deleteHologram(args[2]);
                    sender.sendMessage(
                            Messages.HOLOGRAMS_REMOVED_LINE.toString().replace("%name%", args[2]));
                }

                return true;
            }

            if (args[1].equalsIgnoreCase("move")) {
                if (args.length == 2) {
                    sender.sendMessage(TextUtil.color("&cUsage: /deluxehub hologram move <id>"));
                    return true;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            Messages.HOLOGRAMS_INVALID_HOLOGRAM.toString().replace("%name%", args[2]));
                    return true;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);

                holo.setLocation(player.getLocation());
                sender.sendMessage(Messages.HOLOGRAMS_MOVED.toString().replace("%name%", args[2]));
            }
        }

        return true;
    }
}
