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

package me.zetastormy.akropolis.config.type;

import me.zetastormy.akropolis.util.MapUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@ConfigSerializable
public class CustomInventory {
    public static String HEADER = """
            |      _    _                          _ _
            |     / \\  | | ___ __ ___  _ __   ___ | (_)___
            |    / _ \\ | |/ / '__/ _ \\| '_ \\ / _ \\| | / __|
            |   / ___ \\|   <| | | (_) | |_) | (_) | | \\__ \\
            |  /_/   \\_\\_|\\_\\_|  \\___/| .__/ \\___/|_|_|___/
            |                         |_|
            --------
            SERVER SELECTOR GUI:
            |
            |  The ID of this menu is the file name without extension, e.g. for the default serverselector.yml it's
            | 'serverselector' which you can open using the [MENU] action (e.g. "[MENU] serverselector").
            |  You can create more custom GUIs, just copy this entire file and paste a new one in the menus' directory.
            |  The name of the file is the menu ID.
            --------
            PLAYER HEADS:
            |
            |  You can have player heads, using player names, base64 or HeadDatabase IDs.
            |  Examples:
            |    Username (must have logged into the server once)
            |      material: PLAYER_HEAD
            |      username: <name>
            |
            |    Base64
            |      material: PLAYER_HEAD
            |      base64: <base64 id>
            |
            |    HeadDatabase
            |      material: PLAYER_HEAD
            |      hdb: <hdb id>
            --------
            ITEM FLAGS:
            |
            |  You can add flags to the item (https://jd.papermc.io/paper/1.21.8/org/bukkit/inventory/ItemFlag.html)
            |  Example:
            |    item_flags:
            |      - HIDE_ATTRIBUTES
            |      - HIDE_DESTROYS
            |      - HIDE_ENCHANTS
            |      - HIDE_PLACED_ON
            |      - HIDE_POTION_EFFECTS
            |      - HIDE_UNBREAKABLE
            --------
            ACTIONS:
            |
            |  [MESSAGE] <message> - Send a message to the player
            |  [BROADCAST] <message> - Broadcast a message to everyone
            |  [TITLE] <title;subtitle>[;fade-in][;stay][;fade-out] - Send the player a title message
            |  [ACTIONBAR] <message> - Send an action bar message
            |  [SOUND] <sound> - Send the player a sound
            |  [COMMAND] <command> - Execute a command as the player
            |  [CONSOLE] <command> - Execute a command as console
            |  [GAMEMODE] <gamemode> - Change a players' gamemode
            |  [SERVER] <server> - Send a player to a server
            |  [EFFECT] <effect;level>- Give a potion effect
            |  [MENU] <menu> - Open a menu from (plugins/Akropolis/menus)
            |  [CLOSE] - Close an open inventory
            |
            |  Note: For multi-world servers using Multiverse-Core, use the following action instead of '[SERVER]':
            |    - '[CONSOLE] mvtp <player> world'
            --------
            MESSAGE FORMATTING:
            |
            |  The plugin uses MiniMessage to format the chat,
            |  so you can use tags to color messages, like this: <red> Red colored message!
            |  You can also use HEX colors in an easy way, just like this: <#00ff00>R G B!
            |
            |  More information about MiniMessage can be found here: https://docs.adventure.kyori.net/minimessage/format.html
            |  There's also an online MiniMessage Viewer available: https://webui.adventure.kyori.net/\
            """;

    // Required by Configurate to load data
    public CustomInventory() {}

    public CustomInventory(
        final int slots,
        final String title,
        final Settings.RefreshWithRate refresh,
        final Map<String, ItemRecord> items
    ) {
        this.slots = slots;
        this.title = title;
        this.refresh = refresh;
        this.items = items;
    }

    @Comment("Number of slots in the GUI. Must be a multiple of 9.")
    private int slots = 27;

    @Comment("Title of the GUI.")
    private String title = "Akropolis custom GUI default title";

    @Comment("""
            Automatically update open inventories.
            This can be used to update placeholders in the GUI.\
            """)
    private Settings.RefreshWithRate refresh = new Settings.RefreshWithRate(false, 40);

    @Comment("The items inside the GUI can be listed here.")
    private Map<String, ItemRecord> items = Map.of();

    public int slots() { return this.slots; }
    public String title() { return this.title; }
    public Settings.RefreshWithRate refresh() { return this.refresh; }
    public Map<String, ItemRecord> items() { return this.items; }

    public static CustomInventory defaultServerSelector() {
        return new CustomInventory(
                27,
                "Server Selector",
                new Settings.RefreshWithRate(false, 40),
                MapUtils.linkedHashMapOfEntries(
                        Map.entry("filler", new ItemRecord(
                                "GRAY_STAINED_GLASS_PANE",
                                1,
                                -1,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        )),
                        Map.entry("factions", new ItemRecord(
                                "TNT",
                                1,
                                11,
                                null,
                                null,
                                null,
                                "<yellow><b>Factions",
                                List.of(
                                        "<dark_gray>» <gray>Join now to our coolest server!"
                                ),
                                true,
                                null,
                                null,
                                null,
                                null,
                                null,
                                List.of(
                                        "[CLOSE]",
                                        "[MESSAGE] <dark_gray>| <gray>Sending you to<dark_gray>: <yellow><b>Factions",
                                        "[SERVER] factions"
                                ),
                                null,
                                null,
                                null,
                                null
                        )),
                        Map.entry("survival", new ItemRecord(
                                "GRASS_BLOCK",
                                1,
                                15,
                                null,
                                null,
                                null,
                                "<green><b>Survival",
                                List.of(
                                        "<dark_gray>» <gray>Never gonna give you up!"
                                ),
                                false,
                                null,
                                null,
                                null,
                                null,
                                null,
                                List.of(
                                        "[CLOSE]",
                                        "[MESSAGE] <dark_gray>| <gray>Sending you to<dark_gray>: <green><b>Survival",
                                        "[SERVER] survival"
                                ),
                                null,
                                null,
                                null,
                                null
                        ))
                )
        );
    }

    @ConfigSerializable
    public record ItemRecord(
            @Nullable String material,
            @Nullable Integer amount,
            @Comment("Setting the slot to -1 will fill every empty slot, you can also do 'slots: [0, 1, 2]'")
            @Nullable Integer slot,
            @Nullable List<Integer> slots,
            @Nullable Boolean unbreakable,
            @Nullable String username,
            @Nullable String displayName,
            @Nullable List<String> lore,
            @Nullable Boolean glow,
            @Nullable List<String> itemFlags,
            @Nullable List<String> customModelData,
            @Nullable String customItemModel,
            @Comment("enchantment:level")
            @Nullable List<String> enchantments,
            @Nullable String tooltipStyle,
            @Nullable List<String> actions,
            @Comment("Should we limit how fast a user can use this item? Cooldown is in seconds")
            @Nullable Integer cooldown,
            @Nullable String base64,
            @Nullable String hdb,
            @Nullable String permission
    ) {
        public String username(String def) {
            return Objects.requireNonNullElse(this.username, def);
        }
    }
}
