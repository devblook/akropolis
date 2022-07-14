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
    public void onCommand(CommandSender sender, String label, String[] args) {
        PluginDescriptionFile pdfFile = plugin.getDescription();

        /*
         * Command: help Description: displays help message
         */
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

            if (!sender.hasPermission(Permissions.COMMAND_DELUXEHUB_HELP.getPermission())) {
                sender.sendMessage(TextUtil.parse(
                        "<dark_gray><b> <gray>Server is running <light_purple>DeluxeHub <yellow>v" + pdfFile.getVersion() + " <gray>By <gold>ItsLewizzz"));
                return;
            }

            sender.sendMessage("");
            sender.sendMessage(TextUtil.parse("<light_purple><b>DeluxeHub " + "<white>v" + plugin.getDescription().getVersion()));
            sender.sendMessage(TextUtil.parse("<gray>Author: <white>ItsLewizzz"));
            sender.sendMessage("");
            sender.sendMessage(
                    TextUtil.parse(" <light_purple>/deluxehub info <dark_gray>- <gray><i>Displays information about the current config"));
            sender.sendMessage(TextUtil.parse(" <light_purple>/deluxehub scoreboard <dark_gray>- <gray><i>Toggle the scoreboard"));
            sender.sendMessage(TextUtil.parse(" <light_purple>/deluxehub open <menu> <dark_gray>- <gray><i>Open a custom menu"));
            sender.sendMessage(TextUtil.parse(" <light_purple>/deluxehub hologram <dark_gray>- <gray><i>View the hologram help"));
            sender.sendMessage("");
            sender.sendMessage(TextUtil.parse("  <light_purple>/vanish <dark_gray>- <gray><i>Toggle vanish mode"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/fly <dark_gray>- <gray><i>Toggle flight mode"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/setlobby <dark_gray>- <gray><i>Set the spawn location"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/lobby <dark_gray>- <gray><i>Teleport to the spawn location"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/gamemode <gamemode> <dark_gray>- <gray><i>Set your gamemode"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/gmc <dark_gray>- <gray><i>Go into creative mode"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/gms <dark_gray>- <gray><i>Go into survival mode"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/gma <dark_gray>- <gray><i>Go into adventure mode"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/gmsp <dark_gray>- <gray><i>Go into spectator mode"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/clearchat <dark_gray>- <gray><i>Clear global chat"));
            sender.sendMessage(TextUtil.parse("  <light_purple>/lockchat <dark_gray>- <gray><i>Lock/unlock global chat"));
            sender.sendMessage("");
            return;
        }

        /*
         * Command: reload Description: reloads the entire plugin
         */
        else if (args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission(Permissions.COMMAND_DELUXEHUB_RELOAD.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            long start = System.currentTimeMillis();
            plugin.reload();
            sender.sendMessage(TextUtil.replace(Messages.CONFIG_RELOAD.toComponent(), "time", TextUtil.parse(String.valueOf(System.currentTimeMillis() - start))));
            int inventories = plugin.getInventoryManager().getInventories().size();
            if (inventories > 0) {
                sender.sendMessage(TextUtil.parse("<dark_gray>- <gray>Loaded <green>" + inventories + "<gray> custom menus."));
            }
        }

        /*
         * Command: scoreboard Description: toggles the scoreboard on/off
         */
        else if (args[0].equalsIgnoreCase("scoreboard")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot toggle the scoreboard");
                return;
            }

            if (!sender.hasPermission(Permissions.COMMAND_SCOREBOARD_TOGGLE.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            if (!plugin.getModuleManager().isEnabled(ModuleType.SCOREBOARD)) {
                sender.sendMessage(TextUtil.parse("<red>The scoreboard module is not enabled in the configuration."));
                return;
            }

            Player player = (Player) sender;
            ScoreboardManager scoreboardManager = ((ScoreboardManager) plugin.getModuleManager()
                    .getModule(ModuleType.SCOREBOARD));

            if (scoreboardManager.hasScore(player.getUniqueId())) {
                scoreboardManager.removeScoreboard(player);
                player.sendMessage(TextUtil.replace(Messages.SCOREBOARD_TOGGLE.toComponent(), "value", TextUtil.parse("disabled")));
            } else {
                scoreboardManager.createScoreboard(player);
                player.sendMessage(TextUtil.replace(Messages.SCOREBOARD_TOGGLE.toComponent(), "value", TextUtil.parse("enabled")));
            }
        }

        /*
         * Command: info Description: displays useful information about the
         * configuration
         */
        else if (args[0].equalsIgnoreCase("info")) {

            if (!sender.hasPermission(Permissions.COMMAND_DELUXEHUB_HELP.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            sender.sendMessage("");
            sender.sendMessage(TextUtil.parse("<light_purple><b>Plugin Information"));
            sender.sendMessage("");

            Location location = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY)).getLocation();
            sender.sendMessage(
                    TextUtil.parse("<gray>Spawn set <dark_gray>- " + (location != null ? "<green>yes" : "<red>no <gray><i>(/setlobby)")));

            sender.sendMessage("");

            ModuleManager moduleManager = plugin.getModuleManager();
            sender.sendMessage(TextUtil.parse("<gray>Disabled Worlds (" + moduleManager.getDisabledWorlds().size()
                    + ") <dark_gray>- <green>" + (String.join(", ", moduleManager.getDisabledWorlds()))));

            InventoryManager inventoryManager = plugin.getInventoryManager();
            sender.sendMessage(TextUtil.parse("<gray>Custom menus (" + inventoryManager.getInventories().size() + ")"
                    + " <dark_gray>- <green>" + (String.join(", ", inventoryManager.getInventories().keySet()))));

            HotbarManager hotbarManager = ((HotbarManager) plugin.getModuleManager()
                    .getModule(ModuleType.HOTBAR_ITEMS));
            sender.sendMessage(TextUtil
                    .parse("<gray>Hotbar items (" + hotbarManager.getHotbarItems().size() + ")" + " <dark_gray>- <green>" + (hotbarManager
                            .getHotbarItems().stream().map(HotbarItem::getKey).collect(Collectors.joining(", ")))));

            CommandManager commandManager = plugin.getCommandManager();
            sender.sendMessage(TextUtil.parse("<gray>Custom commands (" + commandManager.getCustomCommands().size() + ")"
                    + " <dark_gray>- <green>" + (commandManager.getCustomCommands().stream()
                    .map(command -> command.getAliases().get(0)).collect(Collectors.joining(", ")))));

            sender.sendMessage("");

            sender.sendMessage(TextUtil.parse("<gray>PlaceholderAPI Hook: "
                    + (plugin.getHookManager().isHookEnabled("PLACEHOLDER_API") ? "<green>yes" : "<red>no")));
            sender.sendMessage(TextUtil.parse("<gray>HeadDatabase Hook: "
                    + (plugin.getHookManager().isHookEnabled("HEAD_DATABASE") ? "<green>yes" : "<red>no")));

            sender.sendMessage("");
        }

        /*
         * Command: open Description: opens a custom menu
         */
        else if (args[0].equalsIgnoreCase("open")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot open menus");
                return;
            }

            if (!sender.hasPermission(Permissions.COMMAND_OPEN_MENUS.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            if (args.length == 1) {
                sender.sendMessage(TextUtil.parse("<red>Usage: /deluxehub open <menu>"));
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

            if (!(sender instanceof Player)) {
                sender.sendMessage("You cannot do this command.");
                return;
            }

            if (!sender.hasPermission(Permissions.COMMAND_HOLOGRAMS.getPermission())) {
                sender.sendMessage(Messages.NO_PERMISSION.toComponent());
                return;
            }

            if (args.length == 1) {
                sender.sendMessage("");
                sender.sendMessage(TextUtil.parse("<light_purple><b>DeluxeHub Holograms"));
                sender.sendMessage("");
                sender.sendMessage(TextUtil.parse(" <light_purple>/" + label + " hologram list"));
                sender.sendMessage(TextUtil.parse("   <gray><i>List all created holograms"));
                sender.sendMessage(TextUtil.parse(" <light_purple>/" + label + " hologram create <id>"));
                sender.sendMessage(TextUtil.parse("   <gray><i>Create a new hologram"));
                sender.sendMessage(TextUtil.parse(" <light_purple>/" + label + " hologram remove <id>"));
                sender.sendMessage(TextUtil.parse("   <gray><i>Delete an existing hologram"));
                sender.sendMessage(TextUtil.parse(" <light_purple>/" + label + " hologram move <id>"));
                sender.sendMessage(TextUtil.parse("   <gray><i>Move the location of a hologram"));
                sender.sendMessage(TextUtil.parse(""));
                sender.sendMessage(TextUtil.parse(" <light_purple>/" + label + " hologram setline <id> <line> <text>"));
                sender.sendMessage(TextUtil.parse("   <gray><i>Set the line of a specific hologram"));
                sender.sendMessage(TextUtil.parse(" <light_purple>/" + label + " hologram addline <id> <text>"));
                sender.sendMessage(TextUtil.parse("   <gray><i>Add a new line to a hologram"));
                sender.sendMessage(TextUtil.parse(" <light_purple>/" + label + " hologram removeline <id> <line>"));
                sender.sendMessage(TextUtil.parse("   <gray><i>Remove a line from a hologram"));
                sender.sendMessage("");
                return;
            }

            Player player = (Player) sender;

            if (args[1].equalsIgnoreCase("list")) {

                if (plugin.getHologramManager().getHolograms().isEmpty()) {
                    sender.sendMessage(Messages.HOLOGRAMS_EMPTY.toComponent());
                    return;
                }

                sender.sendMessage("");
                sender.sendMessage(TextUtil.parse("<light_purple><b>Hologram List"));
                for (Hologram entry : plugin.getHologramManager().getHolograms()) {
                    sender.sendMessage(TextUtil.parse("<dark_gray>- <gray>" + entry.getName()));
                }
                sender.sendMessage("");
            }

            if (args[1].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    sender.sendMessage(TextUtil.parse("<red>Usage: /deluxehub hologram create <id>"));
                    return;
                }

                if (plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_ALREADY_EXISTS.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().createHologram(args[2], player.getLocation());
                List<String> defaultMsg = new ArrayList<>();
                defaultMsg.add("<gray>Created new Hologram called <aqua>" + args[2]);
                defaultMsg.add("<gray>Use <aqua>/deluxehub holo <gray>to customise");
                holo.setLines(defaultMsg);
                sender.sendMessage(TextUtil.replace(Messages.HOLOGRAMS_SPAWNED.toComponent(), "name", TextUtil.parse(args[2])));
                return;
            }

            if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete")) {
                if (args.length == 2) {
                    sender.sendMessage(TextUtil.parse("<red>Usage: /deluxehub hologram remove <id>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                plugin.getHologramManager().deleteHologram(args[2]);
                sender.sendMessage(TextUtil.replace(Messages.HOLOGRAMS_DESPAWNED.toComponent(), "name", TextUtil.parse(args[2])));
                return;
            }

            if (args[1].equalsIgnoreCase("setline")) {
                if (args.length < 5) {
                    sender.sendMessage(TextUtil.parse("<red>Usage: /deluxehub hologram setline <id> <line> <text>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                int line = Integer.parseInt(args[3]);
                String text = TextUtil.joinString(4, args);

                if (holo.hasInvalidLine(line)) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_INVALID_LINE.toComponent(), "line", TextUtil.parse(String.valueOf(line))));
                    return;
                }

                holo.setLine(line, text);
                sender.sendMessage(TextUtil.replace(Messages.HOLOGRAMS_LINE_SET.toComponent(), "line", TextUtil.parse(String.valueOf(line))));
                return;
            }

            if (args[1].equalsIgnoreCase("addline")) {
                if (args.length <= 3) {
                    sender.sendMessage(TextUtil.parse("<red>Usage: /deluxehub hologram addline <id> <text>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                String text = TextUtil.joinString(3, args);

                holo.addLine(text);
                sender.sendMessage(TextUtil.replace(Messages.HOLOGRAMS_ADDED_LINE.toComponent(), "name", TextUtil.parse(args[2])));
            }

            if (args[1].equalsIgnoreCase("removeline")) {
                if (args.length != 4) {
                    sender.sendMessage(TextUtil.parse("<red>Usage: /deluxehub hologram removeline <id> <line>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);
                int line = Integer.parseInt(args[3]);

                if (holo.hasInvalidLine(line)) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_INVALID_LINE.toComponent(), "line", TextUtil.parse(String.valueOf(line))));
                    return;
                }

                if (holo.removeLine(line) == null) {
                    plugin.getHologramManager().deleteHologram(args[2]);
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_REMOVED_LINE.toComponent(), "name", TextUtil.parse(args[2])));
                }

                return;
            }

            if (args[1].equalsIgnoreCase("move")) {
                if (args.length == 2) {
                    sender.sendMessage(TextUtil.parse("<red>Usage: /deluxehub hologram move <id>"));
                    return;
                }

                if (!plugin.getHologramManager().hasHologram(args[2])) {
                    sender.sendMessage(
                            TextUtil.replace(Messages.HOLOGRAMS_INVALID_HOLOGRAM.toComponent(), "name", TextUtil.parse(args[2])));
                    return;
                }

                Hologram holo = plugin.getHologramManager().getHologram(args[2]);

                holo.setLocation(player.getLocation());
                sender.sendMessage(TextUtil.replace(Messages.HOLOGRAMS_MOVED.toComponent(), "name", TextUtil.parse(args[2])));
            }
        }

    }
}
