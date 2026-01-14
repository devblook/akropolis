package me.zetastormy.akropolis.config.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Commands {
    public static String HEADER = """
                _    _                          _ _
               / \\  | | ___ __ ___  _ __   ___ | (_)___
              / _ \\ | |/ / '__/ _ \\| '_ \\ / _ \\| | / __|
             / ___ \\|   <| | | (_) | |_) | (_) | | \\__ \\
            /_/   \\_\\_|\\_\\_|  \\___/| .__/ \\___/|_|_|___/
                                   |_|
            --------
            COMMANDS CUSTOMIZATION:
            
              In this file you can enable or disable commands of the plugin as you like, and even create
              your own custom commands. ATTENTION: You will need to restart your server in order to apply
              changes made to this file!
            --------
            ACTIONS:
            
              [MESSAGE] <message> - Send a message to the player
              [BROADCAST] <message> - Broadcast a message to everyone
              [TITLE] <title;subtitle>[;fade-in][;stay][;fade-out] - Send the player a title message
              [ACTIONBAR] <message> - Send an action bar message
              [SOUND] <sound> - Send the player a sound
              [COMMAND] <command> - Execute a command as the player
              [CONSOLE] <command> - Execute a command as console
              [GAMEMODE] <gamemode> - Change a players' gamemode
              [SERVER] <server> - Send a player to a server
              [EFFECT] <effect;level>- Give a potion effect
              [MENU] <menu> - Open a menu from (plugins/Akropolis/menus)
              [CLOSE] - Close an open inventory
            --------
            MESSAGE FORMATTING:
            
              The plugin uses MiniMessage to format the chat,
              so you can use tags to color messages, like this: <red> Red colored message!
              You can also use HEX colors in an easy way, just like this: <#00ff00>R G B!
            
              More information about MiniMessage can be found here: https://docs.adventure.kyori.net/minimessage/format.html
              There's also an online MiniMessage Viewer available: https://webui.adventure.kyori.net/
            \
            """;

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | CUSTOM COMMANDS                          |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            
            The name of each section is the command name.
            You can create your own commands here and delete the ones you don't want.
            If you don't want any custom command set to 'custom_commands: {}'\
            """)
    private Map<String, CustomCommand> customCommands = Map.ofEntries(
            Map.entry("website", new CustomCommand(
                    null,
                    List.of("web"),
                    List.of("[MESSAGE] <dark_gray>| <click:open_url:'https://www.example.com/'><hover:show_text:'<yellow>Click here to navigate!'><gray>Click here to visit <aqua>www.example.com<gray>!</hover></click>")
            )),
            Map.entry("clearinventory", new CustomCommand(
                    "akropolis.clearinventory",
                    List.of("ci"),
                    List.of(
                            "[CONSOLE] minecraft:clear <player>",
                            "[MESSAGE] <dark_gray>| <yellow>Your inventory has been cleared!"
                    )
            ))
    );

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | AKROPOLIS BUILT-IN COMMANDS              |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            
            The name of each section is the command name.
            Do NOT delete any command section from here.
            You can delete the aliases list or set to 'aliases: []'\
            """)
    private Map<String, BuiltinCommand> commands = Map.ofEntries(
            Map.entry("gamemode", new BuiltinCommand(true, List.of("gm"))),
            Map.entry("gms", new BuiltinCommand(true)),
            Map.entry("gmc", new BuiltinCommand(true)),
            Map.entry("gma", new BuiltinCommand(true)),
            Map.entry("gmsp", new BuiltinCommand(true)),
            Map.entry("clearchat", new BuiltinCommand(true)),
            Map.entry("fly", new BuiltinCommand(true)),
            Map.entry("lockchat", new BuiltinCommand(true, List.of("lc"))),
            Map.entry("setlobby", new BuiltinCommand(true)),
            Map.entry("lobby", new BuiltinCommand(true)),
            Map.entry("vanish", new BuiltinCommand(true, List.of("v")))
    );


    public Map<String, CustomCommand> customCommands() { return this.customCommands; }
    public @NotNull Map<String, BuiltinCommand> commands() { return this.commands; }

    @ConfigSerializable
    public static class CustomCommand {
        @Comment("Players will require this permission to execute this command.")
        private @Nullable String permission = null;

        @Comment("List any aliases for the command here")
        private @Nullable List<String> aliases = null;

        @Comment("Actions to be executed")
        private @Nullable List<String> actions = null;

        // Required by Configurate to load data
        public CustomCommand() {}

        public CustomCommand(
                @Nullable String permission,
                @Nullable List<String> aliases,
                @Nullable List<String> actions
        ) {
            this.permission = permission;
            this.aliases = aliases;
            this.actions = actions;
        }

        public @Nullable String permission() { return this.permission; }
        public @Nullable List<String> aliases() { return this.aliases; }
        public @Nullable List<String> actions() { return this.actions; }
    }

    @ConfigSerializable
    public static class BuiltinCommand {
        private boolean enabled = false;
        @Comment("List any aliases for the command here")
        private @Nullable List<String> aliases = null;

        // Required by Configurate to load data
        public BuiltinCommand() {}

        public BuiltinCommand(boolean enabled, @Nullable List<String> aliases) {
            this.enabled = enabled;
            this.aliases = aliases;
        }

        public BuiltinCommand(boolean enabled) {
            this(enabled, null);
        }

        public boolean enabled() { return this.enabled; }
        public @Nullable List<String> aliases() { return this.aliases; }
    }
}
