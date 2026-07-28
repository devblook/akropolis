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

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import me.zetastormy.akropolis.config.transformation.AbstractTransformation;
import me.zetastormy.akropolis.config.transformation.MessagesTransformations;

import java.util.List;

@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Messages {
    public static String HEADER = """
            |      _    _                          _ _
            |     / \\  | | ___ __ ___  _ __   ___ | (_)___
            |    / _ \\ | |/ / '__/ _ \\| '_ \\ / _ \\| | / __|
            |   / ___ \\|   <| | | (_) | |_) | (_) | | \\__ \\
            |  /_/   \\_\\_|\\_\\_|  \\___/| .__/ \\___/|_|_|___/
            |                         |_|
            --------
            MESSAGES CUSTOMIZATION:
            |
            |  Edit the messages of the plugin as you like. You can change the language, colors and almost everything
            |  you imagine! If there's a message you want to translate, and it isn't here, you can request it to be
            |  translatable by opening a feature request here:
            |  https://github.com/devblook/akropolis/issues/new?assignees=zetastormy&labels=enhancement&template=feature_request.yml&title=A+brief+description+of+your+request
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

    @Comment(AbstractTransformation.VERSION_COMMENT)
    @Setting(value = AbstractTransformation.VERSION_KEY)
    private Integer configVersion = MessagesTransformations.LATEST_VERSION;

    private General general = new General();
    private Help help = new Help();
    private Gamemode gamemode = new Gamemode();
    private Vanish vanish = new Vanish();
    private Flight flight = new Flight();
    private PlayerHider playerHider = new PlayerHider();
    private Lobby lobby = new Lobby();
    private SongPlayer songPlayer = new SongPlayer();
    private FightMode fightMode = new FightMode();
    private Chat chat = new Chat();
    private Scoreboard scoreboard = new Scoreboard();
    private Hotbar hotbar = new Hotbar();
    private DoubleJump doubleJump = new DoubleJump();
    private PlayerMount playerMount = new PlayerMount();
    private WorldEventModifications worldEventModifications = new WorldEventModifications();
    private Holograms holograms = new Holograms();
    private AntiWorldDownloader antiWorldDownloader = new AntiWorldDownloader();


    public General general() { return this.general; }
    public Help help() { return this.help; }
    public Gamemode gamemode() { return this.gamemode; }
    public Vanish vanish() { return this.vanish; }
    public Flight flight() { return this.flight; }
    public PlayerHider playerHider() { return this.playerHider; }
    public Lobby lobby() { return this.lobby; }
    public SongPlayer songPlayer() { return this.songPlayer; }
    public FightMode fightMode() { return this.fightMode; }
    public Chat chat() { return this.chat; }
    public Scoreboard scoreboard() { return this.scoreboard; }
    public Hotbar hotbar() { return this.hotbar; }
    public DoubleJump doubleJump() { return this.doubleJump; }
    public PlayerMount playerMount() { return this.playerMount; }
    public WorldEventModifications worldEventModifications() { return this.worldEventModifications; }
    public Holograms holograms() { return this.holograms; }
    public AntiWorldDownloader antiWorldDownloader() { return this.antiWorldDownloader; }

    @ConfigSerializable
    public static class General {
        private String prefix = "<gold><b>Akropolis <reset><dark_gray>||";
        private String usage = "<dark_gray>» <gray>Usage<dark_gray>: <gold>/<command>";
        private String noPermission = "<prefix> <red>You are forbidden to use this.";
        private String consoleNotAllowed = "<prefix> <red>Console cannot do that.";
        private String customCommandNoPermission = "<prefix> <red>You are not allowed to execute this command.";
        private String invalidPlayer = "<prefix> <yellow><player> <red>is not online!";
        private String configReload = "<prefix> <gray>All configuration files have been reloaded in <green><time>ms<gray>.";
        private String cooldownActive = "<prefix> <red>You must wait <yellow><time>s <red>before doing this again!";

        public String prefix() { return this.prefix; }
        public String usage() { return this.usage; }
        public String noPermission() { return this.noPermission; }
        public String consoleNotAllowed() { return this.consoleNotAllowed; }
        public String customCommandNoPermission() { return this.customCommandNoPermission; }
        public String invalidPlayer() { return this.invalidPlayer; }
        public String configReload() { return this.configReload; }
        public String cooldownActive() { return this.cooldownActive; }
    }

    @ConfigSerializable
    public static class Help {
        private List<String> plugin = List.of(
                "<prefix> <gray>The following commands are available<dark_gray>:",
                "<dark_gray>/<gold>akropolis info <dark_gray>- <gray>Displays information about the current config",
                "<dark_gray>/<gold>akropolis scoreboard <dark_gray>- <gray>Toggle the scoreboard",
                "<dark_gray>/<gold>akropolis hotbar <dark_gray>- <gray>Toggle the hotbar",
                "<dark_gray>/<gold>akropolis holo [option] <dark_gray>- <gray>Displays hologram commands",
                "<dark_gray>/<gold>akropolis open <menu> <dark_gray>- <gray>Opens a custom menu",
                "<dark_gray>/<gold>vanish <dark_gray>- <gray>Toggle vanish mode",
                "<dark_gray>/<gold>fly [player] <dark_gray>- <gray>Toggle flight mode",
                "<dark_gray>/<gold>setlobby <dark_gray>- <gray>Set the spawn location",
                "<dark_gray>/<gold>lobby <dark_gray>- <gray>Teleport to the spawn location",
                "<dark_gray>/<gold>gamemode <gamemode> [player] <dark_gray>- <gray>Set your gamemode",
                "<dark_gray>/<gold>gmc [player] <dark_gray>- <gray>Go into creative mode",
                "<dark_gray>/<gold>gms [player] <dark_gray>- <gray>Go into survival mode",
                "<dark_gray>/<gold>gma [player] <dark_gray>- <gray>Go into adventure mode",
                "<dark_gray>/<gold>gmsp [player] <dark_gray>- <gray>Go into spectator mode",
                "<dark_gray>/<gold>clearchat <dark_gray>- <gray>Clear global chat",
                "<dark_gray>/<gold>lockchat <dark_gray>- <gray>Lock/unlock global chat"
        );

        private List<String> hologram = List.of(
                "<prefix> <gray>The following hologram commands are available<dark_gray>:",
                "<dark_gray>/<gold>akropolis holo list <dark_gray>- <gray>List all created holograms",
                "<dark_gray>/<gold>akropolis holo create <id> <dark_gray>- <gray>Create a new hologram",
                "<dark_gray>/<gold>akropolis holo remove <id> <dark_gray>- <gray>Delete an existing hologram",
                "<dark_gray>/<gold>akropolis holo move <id> <dark_gray>- <gray>Move the location of a hologram",
                "<dark_gray>/<gold>akropolis holo setline <id> <line> <text> <dark_gray>- <gray>Set the line of a specific hologram",
                "<dark_gray>/<gold>akropolis holo addline <id> <text> <dark_gray>- <gray>Add a new line to a hologram",
                "<dark_gray>/<gold>akropolis holo removeline <id> <line> <dark_gray>- <gray>Remove a line from a hologram"
        );

        private List<String> songPlayer = List.of(
                "<prefix> <gray>The following song player commands are available<dark_gray>:",
                "<dark_gray>/<gold>akropolis sp setpos <dark_gray>- <gray>Set the position of the song player",
                "<dark_gray>/<gold>akropolis sp skip <dark_gray>- <gray>Skip a song"
        );

        public List<String> plugin() { return this.plugin; }
        public List<String> hologram() { return this.hologram; }
        public List<String> songPlayer() { return this.songPlayer; }
    }

    @ConfigSerializable
    public static class Gamemode {
        private String gamemodeChange = "<prefix> <gray>You have changed gamemode to <green><gamemode>.";
        private String gamemodeChangeOther = "<prefix> <gray>You have changed gamemode to <green><gamemode> <gray>for <yellow><player><gray>.";
        private String gamemodeInvalid = "<prefix> <green><gamemode> <red>is not a valid gamemode.";

        public String gamemodeChange() { return this.gamemodeChange; }
        public String gamemodeChangeOther() { return this.gamemodeChangeOther; }
        public String gamemodeInvalid() { return this.gamemodeInvalid; }
    }

    @ConfigSerializable
    public static class Vanish {
        private String enable = "<prefix> <green>You have now vanished.";
        private String disable = "<prefix> <red>You have now unvanished.";

        public String enable() { return this.enable; }
        public String disable() { return this.disable; }
    }

    @ConfigSerializable
    public static class Flight {
        private String enable = "<prefix> <green>You have enabled flight.";
        private String disable = "<prefix> <red>You have disabled flight.";
        private String enableOther = "<prefix> <red>You have enabled flight for <yellow><player>.";
        private String disableOther = "<prefix> <red>You have disabled flight for <yellow><player>.";

        public String enable() { return this.enable; }
        public String disable() { return this.disable; }
        public String enableOther() { return this.enableOther; }
        public String disableOther() { return this.disableOther; }
    }

    @ConfigSerializable
    public static class PlayerHider {
        private String hidden = "<prefix> <red>Player visibility disabled.";
        private String shown = "<prefix> <green>Player visibility enabled.";

        public String hidden() { return this.hidden; }
        public String shown() { return this.shown; }
    }

    @ConfigSerializable
    public static class Lobby {
        private String setLobby = "<prefix> <green>You have successfully set the lobby spawn point.";
        private String teleportWorldUnloaded = """
                <prefix> <red>Couldn't teleport you to the lobby because world <gray><world></gray> is not loaded, \
                please contact server administrators.\
                """;
        private String teleportLobbyUnset = """
                <prefix> <red>Couldn't teleport you to the lobby because it's unset, \
                please contact server administrators.\
                """;

        public String setLobby() { return this.setLobby; }
        public String teleportWorldUnloaded() { return this.teleportWorldUnloaded; }
        public String teleportLobbyUnset() { return this.teleportLobbyUnset; }
    }

    @ConfigSerializable
    public static class SongPlayer {
        private String setLocation = "<prefix> <green>You have successfully set the song player location.";
        private String skipped = "<prefix> <green>Song skipped. Now playing <current_song>.";
        private String notLoaded = "<prefix> <red>The song player isn't loaded.";

        public String setLocation() { return this.setLocation; }
        public String skipped() { return this.skipped; }
        public String notLoaded() { return this.notLoaded; }
    }

    @ConfigSerializable
    public static class FightMode {
        private String activateDelay = "<prefix> <gray>Activating fight mode in <red><seconds> second(s).";
        private String deactivateDelay = "<prefix> <gray>Deactivating fight mode in <green><seconds> second(s).";

        public String activateDelay() { return this.activateDelay; }
        public String deactivateDelay() { return this.deactivateDelay; }
    }

    @ConfigSerializable
    public static class Chat {
        private String clearChat = "<dark_gray>| <white>Chat has been cleared by <yellow><player><white>!";
        private String clearChatPlayer = "prefix> <red>Your chat has been cleared by an administrator.";
        private String locked = "<prefix> <red>Chat is currently locked.";
        private String lockedBroadcast = "<prefix> <yellow><player> <red>has locked global chat.";
        private String unlockedBroadcast = "<prefix> <yellow><player> <green>has unlocked global chat.";
        private String antiSwearWordBlocked = "<prefix> <red>Message removed by anti-swear systems.";
        private String antiSwearAdminNotify = "<prefix> <yellow><player> <red>attempted to say<dark_gray>: <aqua><word>.";
        private String commandBlocked = "<prefix> <red>This command has been restricted.";

        public String clearChat() { return this.clearChat; }
        public String clearChatPlayer() { return this.clearChatPlayer; }
        public String locked() { return this.locked; }
        public String lockedBroadcast() { return this.lockedBroadcast; }
        public String unlockedBroadcast() { return this.unlockedBroadcast; }
        public String antiSwearWordBlocked() { return this.antiSwearWordBlocked; }
        public String antiSwearAdminNotify() { return this.antiSwearAdminNotify; }
        public String commandBlocked() { return this.commandBlocked; }
    }

    @ConfigSerializable
    public static class Scoreboard {
        private String enable = "<prefix> <gray>You have <yellow>enabled <gray>the scoreboard.";
        private String disable = "<prefix> <gray>You have <yellow>disabled <gray>the scoreboard.";

        public String enable() { return this.enable; }
        public String disable() { return this.disable; }
    }

    @ConfigSerializable
    public static class Hotbar {
        private String enable = "<prefix> <gray>You have <yellow>enabled <gray>the hotbar items.";
        private String disable = "<prefix> <gray>You have <yellow>disabled <gray>the hotbar items.";

        public String enable() { return this.enable; }
        public String disable() { return this.disable; }
    }

    @ConfigSerializable
    public static class DoubleJump {
        private String cooldownActive = "<prefix> <red>You must wait <yellow><time>s <red>to double jump again!";

        public String cooldownActive() { return this.cooldownActive; }
    }

    @ConfigSerializable
    public static class PlayerMount {
        private String cooldownActive = "<prefix> <red>You must wait <yellow><time>s <red>to mount a player again!";

        public String cooldownActive() { return this.cooldownActive; }
    }

    @ConfigSerializable
    public static class WorldEventModifications {
        private String itemDrop = "<prefix> <red>You are not allowed to drop items.";
        private String itemPickup = "<prefix> <red>You are not allowed to pickup items.";
        private String blockPlace = "<prefix> <red>You are not allowed to place blocks.";
        private String blockBreak = "<prefix> <red>You are not allowed to break blocks.";
        private String blockInteract = "<prefix> <red>You are not allowed to use this.";
        private String playerPvp = "<prefix> <red>You are not allowed to pvp here.";

        public String itemDrop() { return this.itemDrop; }
        public String itemPickup() { return this.itemPickup; }
        public String blockPlace() { return this.blockPlace; }
        public String blockBreak() { return this.blockBreak; }
        public String blockInteract() { return this.blockInteract; }
        public String playerPvp() { return this.playerPvp; }
    }

    @ConfigSerializable
    public static class Holograms {
        private String empty = "<prefix> <red>Could not find any holograms.";
        private String alreadyExists = "<prefix> <red>There is a hologram with this name already <yellow>(<name>)<red>.";
        private String invalidHologram = "<prefix> <red>There is no hologram with this name <yellow>(<name>)<red>.";
        private String invalidLine = "<prefix> <red>Could not find hologram line.";
        private String spawned = "<prefix> <gray>Created new hologram with name <yellow><name><gray>.";
        private String despawned = "<prefix> <gray>Removed hologram with name <yellow><name><gray>.";
        private String moved = "<prefix> <gray>Moved hologram <yellow><name><gray>.";
        private String lineSet = "<prefix> <gray>Set line <yellow><line><gray>.";
        private String addedLine = "<prefix> <gray>Added new line.";
        private String removedLine = "<prefix> <gray>Removed line <yellow><line><gray>.";

        public String empty() { return this.empty; }
        public String alreadyExists() { return this.alreadyExists; }
        public String invalidHologram() { return this.invalidHologram; }
        public String invalidLine() { return this.invalidLine; }
        public String spawned() { return this.spawned; }
        public String despawned() { return this.despawned; }
        public String moved() { return this.moved; }
        public String lineSet() { return this.lineSet; }
        public String addedLine() { return this.addedLine; }
        public String removedLine() { return this.removedLine; }
    }

    @ConfigSerializable
    public static class AntiWorldDownloader {
        private String adminNotify = "<prefix> <white><player> <red>has joined with the World Downloader mod, their ability to use it has been restricted.";

        public String adminNotify() { return this.adminNotify; }
    }
}
