package me.zetastormy.akropolis.config.type;

import me.zetastormy.akropolis.util.MapUtils;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Settings {
    public static String HEADER = """
            |      _    _                          _ _
            |     / \\  | | ___ __ ___  _ __   ___ | (_)___
            |    / _ \\ | |/ / '__/ _ \\| '_ \\ / _ \\| | / __|
            |   / ___ \\|   <| | | (_) | |_) | (_) | | \\__ \\
            |  /_/   \\_\\_|\\_\\_|  \\___/| .__/ \\___/|_|_|___/
            |                         |_|
            --------
            CUSTOM MENUS:
            |
            |  You can add more menus to the "Akropolis/menus" directory,
            |  copy and paste the default, server selector, menu and edit the file.
            |  Use the name of the file as the action ID.
            --------
            BUILT IN PLUGIN PLACEHOLDERS:
            |
            |  <player> - Returns player name
            |  <location> - Returns player location
            |  <online> - Returns number of players online
            |  <online_max> - Returns number of max player slots
            |  <world> - Returns player world name
            |  <ping> - Returns player ping
            |  <current_song> - Returns the current song title
            |
            |  Use PlaceholderAPI to get more: https://www.spigotmc.org/resources/placeholderapi.6245/
            |  PlaceholderAPI's placeholders use the following format: <papi:placeholder>
            |  Example: <papi:luckperms_prefix>
            |
            |  If you prefer a more modern alternative to PlaceholderAPI, Akropolis is also compatible with MiniPlaceholders
            |  by 4drian3d, which uses a more easy to read format. Example: <luckperms_prefix>
            |  For more information see: https://github.com/MiniPlaceholders/MiniPlaceholders/wiki/User-Getting-Started
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
            |  [EFFECT] <effect;level> - Give a potion effect
            |  [MENU] <menu> - Open a menu from (plugins/Akropolis/menus)
            |  [CLOSE] - Close an open inventory
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

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | GENERAL SETTINGS                         |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            
            List your worlds you don't want Akropolis to manage.\
            """)
    private DisabledWorlds disabledWorlds = new DisabledWorlds();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | ANTI-WORLD DOWNLOADER                    |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            
            Prevent users downloading your world via the world downloader mod.
            There is no need to kick the player if he is running WDL as our system hooks into the mod to prevent a world download.
            Note: this only blocks the official World Downloader mod which allows blocking by the server, there is no way of
            blocking stealth world downloading mods since the world chunks are downloaded by the player to be able to see the
            world.\
            """)
    private AntiWDL antiWdl = new AntiWDL();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | SONG PLAYER                             |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            
            Plays music through noteblocks using NoteBlock API. Music files are stored in nbs format inside "Akropolis/songs"
            directory.\
            """)
    private SongPlayer songPlayer = new SongPlayer();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | NAMETAG                                  |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private Nametag nametag = new Nametag();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | SCOREBOARD                               |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private Scoreboard scoreboard = new Scoreboard();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            |TABLIST                                   |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private Tablist tablist = new Tablist();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | AUTO BROADCAST                           |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private Announcements announcements = new Announcements();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | BOSS BAR AUTO BROADCAST                  |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private BossBarAnnouncements bossBarAnnouncements = new BossBarAnnouncements();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | LAUNCHPAD                                |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private Launchpad launchpad = new Launchpad();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | PLAYER MOUNT                             |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            
            Players with the akropolis.player.mount will be able to mount other players.\
            """)
    private PlayerMount playerMount = new PlayerMount();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | DOUBLE JUMP                              |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private DoubleJump doubleJump = new DoubleJump();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | FLY                                      |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private Fly fly = new Fly();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | CHAT MANAGEMENT                          |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private ChatManagement chatManagement = new ChatManagement();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | COMMAND BLOCKER                          |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private CommandBlock commandBlock = new CommandBlock();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | ANTI SWEAR                               |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private AntiSwear antiSwear = new AntiSwear();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | WORLD EVENT SETTINGS                     |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private WorldSettings worldSettings = new WorldSettings();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | PLAYER JOIN AND LEAVE MESSAGES           |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private JoinLeaveMessages joinLeaveMessages = new JoinLeaveMessages();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | ACTIONS EXECUTED UPON JOIN               |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private List<String> joinEvents = List.of(
            "[MESSAGE] <dark_gray><st>+---------------***---------------+",
            "[MESSAGE] <reset>",
            "[MESSAGE] <dark_gray>| <gray>Welcome, <gold><b><player><reset> <gray>to the server!",
            "[MESSAGE] <reset>",
            "[MESSAGE] <dark_gray>» <gray>Website<dark_gray>: <yellow>www.example.com",
            "[MESSAGE] <dark_gray>» <gray>Store<dark_gray>: <yellow>store.example.com",
            "[MESSAGE] <dark_gray>» <gray>Discord<dark_gray>: <yellow>discord.example.com",
            "[MESSAGE] <reset>",
            "[MESSAGE]             <gray><i>Powered by Akropolis",
            "[MESSAGE] <dark_gray><st>+---------------***---------------+",
            "[TITLE] <yellow><b>WELCOME;<white><player>;1;2;1",
            "[SOUND] ENTITY_PLAYER_LEVELUP",
            "[GAMEMODE] survival",
            "[EFFECT] SPEED;1"
    );

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | JOIN SETTINGS                            |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private JoinSettings joinSettings = new JoinSettings();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | CUSTOM JOIN ITEMS                        |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private CustomJoinItems customJoinItems = new CustomJoinItems();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | FIGHT MODE                               |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private FightMode fightMode = new FightMode();

    @Comment("""
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            | PLAYER HIDER                             |
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            \
            """)
    private PlayerHider playerHider = new PlayerHider();


    // Shared classes

    @ConfigSerializable
    public static class RefreshWithRate {
        private boolean enabled = true;

        @Comment("In ticks (20 ticks = 1 second).")
        private int rate;

        // Required by Configurate to load data
        @SuppressWarnings("unused")
        public RefreshWithRate() {}

        public RefreshWithRate(int defaultRate) { this.rate = defaultRate; }

        public boolean enabled() { return this.enabled; }
        public int rate() { return this.rate; }
    }

    @ConfigSerializable
    public static class Sound {
        private boolean enabled;
        private String value;
        private double volume;
        private double pitch;

        // Required by Configurate to load data
        @SuppressWarnings("unused")
        public Sound() {}

        public Sound(boolean enabled, String value, double volume, double pitch) {
            this.enabled = enabled;
            this.value = value;
            this.volume = volume;
            this.pitch = pitch;
        }

        public boolean enabled() { return this.enabled; }
        public String value() { return this.value; }
        public double volume() { return this.volume; }
        public double pitch() { return this.pitch; }
    }

    @ConfigSerializable
    public record ItemRecord(
            @Nullable String material,
            @Nullable Integer amount,
            @Nullable Integer slot,
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

    public DisabledWorlds disabledWorlds() { return this.disabledWorlds; }
    public AntiWDL antiWdl() { return this.antiWdl; }
    public SongPlayer songPlayer() { return this.songPlayer; }
    public Nametag nametag() { return this.nametag; }
    public Scoreboard scoreboard() { return this.scoreboard; }
    public Tablist tablist() { return this.tablist; }
    public Announcements announcements() { return this.announcements; }
    public BossBarAnnouncements bossBarAnnouncements() { return this.bossBarAnnouncements; }
    public Launchpad launchpad() { return this.launchpad; }
    public PlayerMount playerMount() { return this.playerMount; }
    public DoubleJump doubleJump() { return this.doubleJump; }
    public Fly fly() { return this.fly; }
    public ChatManagement chatManagement() { return this.chatManagement; }
    public CommandBlock commandBlock() { return this.commandBlock; }
    public AntiSwear antiSwear() { return this.antiSwear; }
    public WorldSettings worldSettings() { return this.worldSettings; }
    public JoinLeaveMessages joinLeaveMessages() { return this.joinLeaveMessages; }
    public List<String> joinEvents() { return this.joinEvents; }
    public JoinSettings joinSettings() { return this.joinSettings; }
    public CustomJoinItems customJoinItems() { return this.customJoinItems; }
    public FightMode fightMode() { return this.fightMode; }
    public PlayerHider playerHider() { return this.playerHider; }


    @ConfigSerializable
    public static class DisabledWorlds {
        @Comment("Should we invert the list making it a whitelist rather than a blacklist?")
        private boolean invert = false;

        @Comment("List your worlds here, set \"worlds: []\" to disable.")
        private List<String> worlds = List.of("world_nether");

        public boolean invert() { return this.invert; }
        public List<String> worlds() { return this.worlds; }
    }

    @ConfigSerializable
    public static class AntiWDL {
        @Comment("Should AntiWDL be enabled?")
        private boolean enabled = true;

        @Comment("Should players with 'akropolis.antiwdl.alert' will be notified?")
        private boolean notifyAdmins = true;

        public boolean enabled() { return this.enabled; }
        public boolean notifyAdmins() { return this.notifyAdmins; }

    }

    @ConfigSerializable
    public static class SongPlayer {
        @Comment("Should we play music in the lobby?")
        private boolean enabled = false;

        @Comment("""
                There are 2 types of song players:
                - RADIO: plays songs for all players no matter where they are.
                - POSITION: play songs for all added players in specified range from specified point.\
                """)
        private Type type = Type.RADIO;

        @Comment("""
                Modify the range where a song is played while using POSITION song player.
                Use the "/akro sp setpos" command to set the position of the song player.\
                """)
        private int distance = 16;

        @Comment("Set the volume of the song player.")
        private int volume = 85;

        @Comment("Fade effect duration in ticks (20 ticks = 1 second).")
        private Fade fade = new Fade();

        @Comment("Actions to perform when a song starts playing.")
        private List<String> actions = List.of("[ACTIONBAR] <gray>Now playing<dark_gray>: <yellow><current_song>");


        public boolean enabled() { return this.enabled; }
        public Type type() { return this.type; }
        public int distance() { return this.distance; }
        public int volume() { return this.volume; }
        public Fade fade() { return this.fade;}
        public List<String> actions() { return this.actions; }


        public enum Type {
            RADIO, POSITION
        }

        @ConfigSerializable
        public static class Fade {
            private int in = 20;
            private int out = 20;

            public int in() { return in; }
            public int out() { return out; }
        }
    }

    @ConfigSerializable
    public static class Nametag {
        @Comment("Should the nametag feature be enabled?")
        private boolean enabled = false;

        @Comment("Should we refresh the nametag (update placeholders)?")
        private RefreshWithRate refresh = new RefreshWithRate(200);

        @Comment("""
                Configure the nametag format.
                For more complex use cases it is encouraged to use
                another plugin that is merely dedicated to this kind of features.\
                """)
        private Format format = new Format();


        public boolean enabled() { return this.enabled; }
        public RefreshWithRate refresh() { return this.refresh; }
        public Format format() { return this.format; }


        @ConfigSerializable
        public static class Format {
            private String prefix = "<papi:luckperms_prefix> ";

            @Comment("""
                    Use the HEX color of your preference,
                    the nearest Minecraft named color will be used.\
                    """)
            private String nameColor = "#FFFFFF";
            private String suffix = " <yellow><ping>";


            public String prefix() { return this.prefix; }
            public String nameColor() { return this.nameColor; }
            public String suffix() { return this.suffix; }
        }
    }

    @ConfigSerializable
    public static class Scoreboard {
        @Comment("Should the scoreboard feature be enabled?")
        private boolean enabled = true;

        @Comment("""
                Should we delay showing the scoreboard?
                Value in ticks (20 ticks = 1 second), 0 to disable.\
                """)
        private DisplayDelay displayDelay = new DisplayDelay();

        @Comment("""
                Should we refresh the scoreboard (update placeholders)?
                """)
        private RefreshWithRate refresh = new RefreshWithRate(200);

        @Comment("""
                Scoreboard title. There is no character limit since Minecraft 1.18.
                """)
        private String title = "<gold><b>Akropolis <reset><dark_gray>(<yellow><online><dark_gray>/<yellow><online_max><dark_gray>)";

        private List<String> lines = List.of(
                "",
                "<dark_gray>» <gray>Player<dark_gray>: <yellow><player>",
                "<dark_gray>» <gray>Ping<dark_gray>: <yellow><ping>ms",
                "",
                "    <yellow>play.example.com"
        );


        public boolean enabled() { return this.enabled; }
        public DisplayDelay displayDelay() { return this.displayDelay; }
        public RefreshWithRate refresh() { return this.refresh; }
        public String title() { return this.title; }
        public List<String> lines() { return this.lines; }


        @ConfigSerializable
        public static class DisplayDelay {
            @Comment("Default 60 ticks = 3 seconds")
            private int serverEnter = 60;

            @Comment("Default 40 ticks = 2 seconds")
            private int worldChange = 40;


            public int serverEnter() { return this.serverEnter; }
            public int worldChange() { return this.worldChange; }
        }
    }

    @ConfigSerializable
    public static class Tablist {
        @Comment("Should the tablist feature be enabled?")
        private boolean enabled = true;

        @Comment("Should we refresh the tablist (update placeholders)?")
        private RefreshWithRate refresh = new RefreshWithRate(400);

        @Comment("Modify the header of the tablist.")
        private List<String> header = List.of(
                "",
                "<dark_gray>» <gold><b>Akropolis <reset><dark_gray>(<yellow><online><dark_gray>/<yellow><online_max><dark_gray>) <dark_gray>«",
                ""
        );

        @Comment("Modify the footer of the tablist.")
        private List<String> footer = List.of(
                "",
                "    <gray>Website <dark_gray>| <yellow>www.example.com    ",
                "    <gray>Store <dark_gray>| <yellow>store.example.com    ",
                ""
        );


        public boolean enabled() { return this.enabled; }
        public RefreshWithRate refresh() { return this.refresh; }
        public List<String> header() { return this.header; }
        public List<String> footer() { return this.footer; }
    }

    @ConfigSerializable
    public static class Announcements {
        @Comment("Should the announcements' system be enabled?")
        private boolean enabled = true;

        @Comment("Delay sending announcements (in seconds).")
        private int delay = 60;

        @Comment("How many players should be required to send announcements?")
        private int requiredPlayers = 1;

        private Sound sound = new Sound(true, "BLOCK_NOTE_BLOCK_PLING", 1.0, 1.0);

        @Comment("Announcement list.")
        private Map<String, List<String>> announcements = MapUtils.linkedHashMapOfEntries(
                Map.entry("test1", List.of(
                        "<reset>",
                        "<blue><b>INFORMATION</b> <dark_gray>- Akropolis default announcements",
                        "<click:open_url:'https://github.com/devblook/akropolis/'><hover:show_text:'<yellow>Click here to see the source code!'><dark_gray>» <gray>The source code of the plugin is available on <white>GitHub<gray>.</hover></click>",
                        "<reset>"
                )),
                Map.entry("test2", List.of(
                        "<reset>",
                        "<gold><b>ANNOUNCEMENT</b> <dark_gray>- Akropolis default announcements",
                        "<click:open_url:'https://github.com/zetastormy/'><hover:show_text:'<yellow>Click here to navigate to my GitHub!'><dark_gray>» <gray>This plugin was forked by <yellow>ZetaStormy<gray>.</hover></click>",
                        "<reset>"
                )),
                Map.entry("test3", List.of(
                        "<reset>",
                        "<yellow><b>TIP</b> <dark_gray>- Akropolis default announcements",
                        "<dark_gray>» <gray>Configure these messages in the <aqua>config.yml<gray>.",
                        "<reset>"
                )),
                Map.entry("test4", List.of(
                        "<reset>",
                        "<blue><b>INFORMATION</b> <dark_gray>- Akropolis default announcements",
                        "<click:open_url:'https://github.com/devblook/'><hover:show_text:'<yellow>Click here to navigate to our GitHub!'><dark_gray>» <gray>This plugin is maintained by <color:#1e2f45>Dev<white>Blook <gray>Team.</hover></click>",
                        "<reset>"
                )),
                Map.entry("test5", List.of(
                        "<reset>",
                        "<yellow><b>TIP</b> <dark_gray>- Akropolis default announcements",
                        "<click:open_url:'https://discord.gg/w438z8TKej'><hover:show_text:'<yellow>Click here to join our Discord server!'><dark_gray>» <gray>Click here to join <color:#1e2f45>Dev<white>Blook <gray>Team's Discord server to get support and update announcements of all our projects.</hover></click>",
                        "<reset>"
                ))
        );


        public boolean enabled() { return this.enabled; }
        public int delay() { return this.delay; }
        public int requiredPlayers() { return this.requiredPlayers; }
        public Sound sound() { return this.sound; }
        public Map<String, List<String>> announcements() { return this.announcements; }
    }

    @ConfigSerializable
    public static class BossBarAnnouncements {
        @Comment("Should the boss bar announcements' system be enabled?")
        private boolean enabled = false;

        @Comment("Delay sending boss bar announcements (in seconds).")
        private int delay = 60;

        @Comment("Choose how the boss bar looks.")
        private Overlay overlay = new Overlay();

        private Sound sound = new Sound(false, "BLOCK_NOTE_BLOCK_PLING", 1.0, 1.0);
        private List<String> announcements = List.of(
                "<blue><b>THIS SERVER USES AKROPOLIS",
                "<gold><b>NOW WITH BOSS BAR ANNOUNCEMENTS!",
                "<gradient:green:blue><b>MINIMESSAGE SUPPORT!"
        );

        @ConfigSerializable
        public static class Overlay {
            @Comment("""
                    Decide if the boss bar is continuous or split into segments.
                    Valid options: PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20\
                    """)
            private BossBar.Overlay type = BossBar.Overlay.PROGRESS;

            @Comment("The amount of boss bar that's filled up. A number in the interval [0, 1].")
            private double progress = 1.0;


            public BossBar.Overlay type() { return this.type; }
            public double progress() { return this.progress; }
        }

        public boolean enabled() { return this.enabled; }
        public int delay() { return this.delay; }
        public Overlay overlay() { return this.overlay; }
        public Sound sound() { return this.sound; }
        public List<String> announcements() { return this.announcements; }

    }

    @ConfigSerializable
    public static class Launchpad {
        @Comment("Should the launchpad feature be enabled?")
        private boolean enabled = true;

        @Comment("Launch power for launchpad (max 4.0).")
        private double launchPower = 3.0;
        private double launchPowerY = 1.0;

        @Comment("""
                Top and bottom block required to make a launchpad.
                For performance reasons, the top block must be an interactable block (pressure plate),
                otherwise the launchpad will not work.\
                """)
        private String topBlock = "STONE_PRESSURE_PLATE";
        private String bottomBlock = "REDSTONE_BLOCK";

        @Comment("Actions executed upon use of a launchpad.")
        private List<String> actions = List.of(
                "[SOUND] ENTITY_BAT_TAKEOFF"
        );


        public boolean enabled() { return this.enabled; }
        public double launchPower() { return this.launchPower; }
        public double launchPowerY() { return this.launchPowerY; }
        public String topBlock() { return this.topBlock; }
        public String bottomBlock() { return this.bottomBlock; }
        public List<String> actions() { return this.actions; }
    }

    @ConfigSerializable
    public static class PlayerMount {
        @Comment("Should the player mount feature be enabled?")
        private boolean enabled = false;

        @Comment("Cooldown time in seconds (0 to disable).")
        private int cooldown = 5;

        @Comment("Actions executed upon mounting a player.")
        private List<String> actions = List.of(
                "[SOUND] ENTITY_HORSE_SADDLE"
        );

        public boolean enabled() { return this.enabled; }
        public int cooldown() { return this.cooldown; }
        public List<String> actions() { return this.actions; }
    }

    @ConfigSerializable
    public static class DoubleJump {
        @Comment("Should the double jump feature be enabled?")
        private boolean enabled = true;

        @Comment("Launch power for double jump (max 4.0).")
        private double launchPower = 1.0;
        private double launchPowerY = 0.9;

        @Comment("""
                Toggles whether the player has to be on the ground or not to use the double jump feature.
                If you disable this option, players will be able to fly by repeatedly using the double jump option.\
                """)
        private boolean onGround = true;

        @Comment("Cooldown time in seconds (0 to disable).")
        private int cooldown = 3;

        @Comment("Actions executed upon use of double jump.")
        private List<String> actions = List.of(
                "[SOUND] ENTITY_BAT_TAKEOFF"
        );


        public boolean enabled() { return this.enabled; }
        public double launchPower() { return this.launchPower; }
        public double launchPowerY() { return this.launchPowerY; }
        public boolean onGround() { return this.onGround; }
        public int cooldown() { return this.cooldown; }
        public List<String> actions() { return this.actions; }
    }

    @ConfigSerializable
    public static class Fly {
        @Comment("""
                Choose to save the fly state of every player in data.yml across reboots (might impact performance).
                This option conflicts with double jump so if you enable it, players who toggle their fly won't
                be able to use the double jump feature.\
                """)
        private boolean saveState = false;

        @Comment("""
                This will force the fly to be enabled on join (if player has the permission to use the fly command).
                This option also respects the preference (if save_state is true) of the user if he disables the fly mode.\
                """)
        private boolean forceOnJoin = false;

        public boolean saveState() { return this.saveState; }
        public boolean forceOnJoin() { return this.forceOnJoin; }
    }

    @ConfigSerializable
    public static class ChatManagement {
        @Comment("""
                If you want to disable this feature temporally, just deny the akropolis.chat.group.default
                permission in LuckPerms or whichever permission management plugin you use. Otherwise, use the option below.\
                """)
        private boolean enabled = false;
        @Comment("""
                Create all the group formats you want following the same template.
                To use any of these formats, the player must have the permission
                akropolis.chat.group.<group name>\
                """)
        private Map<String, ChatGroup> groups = MapUtils.linkedHashMapOfEntries(
                Map.entry(
                        "default", new ChatGroup(
                                0,
                                "<hover:show_text:'<rainbow>You can also use hover here!'><papi:luckperms_prefix></hover> <gray><player> <dark_gray>» <gray><message>",
                                new ChatGroup.Cooldown(
                                        3,
                                        "<gold><b>Akropolis <reset><dark_gray>|| <red>Please wait <yellow><time>s <red>before talking again!"
                                ),
                                MapUtils.linkedHashMapOfEntries(
                                        Map.entry("smile", new ChatGroup.Emoji(
                                                List.of(":)", ":smile:"),
                                                List.of("<yellow>😀<reset>")
                                        )),
                                        Map.entry("sunglasses", new ChatGroup.Emoji(
                                                List.of("B)", ":sunglasses:"),
                                                List.of("<yellow>😎<reset>")
                                        )),
                                        Map.entry("heart", new ChatGroup.Emoji(
                                                List.of("<3", ":heart:"),
                                                List.of("<red>❤<reset>")
                                        ))
                                )
                        )
                ),
                Map.entry(
                        "vip", new ChatGroup(
                                1,
                                "<papi:luckperms_prefix> <green><player> <dark_gray>» <white><message>",
                                new ChatGroup.Cooldown(
                                        1,
                                        "<gold><b>Akropolis <reset><dark_gray>|| <red>Please wait <yellow><time>s <red>before talking again!"
                                ),
                                MapUtils.linkedHashMapOfEntries(
                                        Map.entry("tableflip", new ChatGroup.Emoji(
                                                List.of(":tableflip:"),
                                                List.of("<red>(╯°□°）╯<white>︵<gray> ┻━┻<reset>")
                                        )),
                                        Map.entry("wave", new ChatGroup.Emoji(
                                                List.of("o/", ":wave:"),
                                                List.of("<green>( ﾟ◡ﾟ)/<reset>")
                                        )),
                                        Map.entry("music", new ChatGroup.Emoji(
                                                List.of(":music:"),
                                                List.of(
                                                        "<red>♫<reset>",
                                                        "<blue>♬<reset>",
                                                        "<dark_aqua>♪<reset>",
                                                        "<dark_purple>♩<reset>",
                                                        "<green>♭<reset>",
                                                        "<yellow>♪<reset>"
                                                )
                                        ))
                                )
                        )
                ),
                Map.entry(
                        "staff", new ChatGroup(
                                2,
                                "<papi:luckperms_prefix> <red><player> <dark_gray>» <white><message>",
                                new ChatGroup.Cooldown(),
                                Map.of()
                        )
                )
        );

        public boolean enabled() { return this.enabled; }
        public Map<String, ChatGroup> groups() { return this.groups; }

        @ConfigSerializable
        public static class ChatGroup {
            @Comment("""
                    This is used to determine which group is used when a player has multiple groups' permissions.
                    Groups with higher priority take precedence.\
                    """)
            private int priority = 0;
            private String format = null;

            @Comment("Just in case you want to slow down your players a little.")
            private Cooldown cooldown = new Cooldown();

            @Comment("Add all the emojis you want here. These emojis will only be available for this group.")
            private Map<String, Emoji> emojis = Map.of();

            // Required by Configurate to load data
            @SuppressWarnings("unused")
            public ChatGroup() {}

            public ChatGroup(int priority, String format, Cooldown cooldown, Map<String, Emoji> emojis) {
                this.priority = priority;
                this.format = format;
                this.cooldown = cooldown;
                this.emojis = emojis;
            }

            public int priority() { return this.priority; }
            public String format(String groupName) {
                return Objects.requireNonNullElse(
                        this.format,
                        String.format("<red><bold>[Akropolis]</bold> Chat group <yellow>%s</yellow> has no format, check the configuration.", groupName)
                );
            }
            public Cooldown cooldown() { return this.cooldown; }
            public Map<String, Emoji> emojis() { return this.emojis; }


            @ConfigSerializable
            public static class Cooldown {
                @Comment("Time is in seconds.")
                private int time = 0;
                private String message = null;

                // Required by Configurate to load data
                @SuppressWarnings("unused")
                public Cooldown() {}

                public Cooldown(int time, String message) {
                    this.time = time;
                    this.message = message;
                }

                public int time() { return this.time; }
                public String message() {
                    return Objects.requireNonNullElse(
                            this.message,
                            "<red><bold>[Akropolis]</bold> Chat group <yellow>%s</yellow> has no cooldown message, check the configuration."
                    );
                }
            }

            @ConfigSerializable
            public static class Emoji {
                @Comment("Players have to type this to use the emoji.")
                private List<String> emoticon;
                @Comment("""
                        The emoticon will be replaced by a random emoji of the list below.
                        If you don't want multiple random emojis just set one so it will always use it.\
                        """)
                private List<String> emoji;

                // Required by Configurate to load data
                @SuppressWarnings("unused")
                public Emoji() {}

                public Emoji(List<String> emoticon, List<String> emoji) {
                    this.emoticon = emoticon;
                    this.emoji = emoji;
                }

                public List<String> emoticon() { return this.emoticon; }
                public List<String> emoji() { return this.emoji; }
            }
        }
    }

    @ConfigSerializable
    public static class CommandBlock {

        @Comment("Should the command blocker feature be enabled?")
        private boolean enabled = true;

        @Comment("List of commands that will be blocked.")
        private List<String> blockedCommands = List.of(
                "/pl",
                "/plugins",
                "/?",
                "/bukkit:?",
                "/bukkit:plugins",
                "/bukkit:pl",
                "/bukkit:help",
                "/ver",
                "/version",
                "/bukkit:version",
                "/bukkit:ver"
        );

        public boolean enabled() { return this.enabled; }
        public List<String> blockedCommands() { return this.blockedCommands; }
    }

    @ConfigSerializable
    public static class AntiSwear {
        @Comment("Should the anti-swear feature be enabled?")
        private boolean enabled = true;

        @Comment("List of words that will be blocked.")
        private List<String> blockedWords = List.of(
                "fuck",
                "shit",
                "bitch"
        );

        public boolean enabled() { return this.enabled; }
        public List<String> blockedWords() { return this.blockedWords; }
    }

    @ConfigSerializable
    public static class WorldSettings {
        @Comment("""
                Player related
                Inventory drop is on death\
                """)
        private boolean disableInventoryDrop = true;
        private boolean disableHungerLoss = true;
        private boolean disableFallDamage = true;
        private boolean disablePlayerPvp = true;
        private boolean disableVoidDeath = true;
        private boolean disableFireDamage = true;

        @Comment("Cactus, dripstone and others")
        private boolean disableContactDamage = true;
        private boolean disableDrowning = true;
        private boolean disableOffHandSwap = true;

        @Comment("Misc")
        private boolean disableWeatherChange = true;
        private boolean disableDeathMessage = true;
        private boolean disableMobSpawning = true;

        @Comment("Item entities")
        private boolean disableItemDrop = true;
        private boolean disableItemPickup = true;

        @Comment("Disables inventory movement, overrides custom items setting")
        private boolean disableInventoryMovement = true;

        @Comment("Blocks")
        private boolean disableBlockBreak = true;
        private boolean disableBlockPlace = true;

        @Comment("Chest, furnace, crop trample, etc")
        private boolean disableBlockInteract = true;
        private boolean disableBlockBurn = true;
        private boolean disableBlockFireSpread = true;
        private boolean disableBlockLeafDecay = true;

        public boolean disableInventoryDrop() { return this.disableInventoryDrop; }
        public boolean disableHungerLoss() { return this.disableHungerLoss; }
        public boolean disableFallDamage() { return this.disableFallDamage; }
        public boolean disablePlayerPvp() { return this.disablePlayerPvp; }
        public boolean disableVoidDeath() { return this.disableVoidDeath; }
        public boolean disableFireDamage() { return this.disableFireDamage; }
        public boolean disableContactDamage() { return this.disableContactDamage; }
        public boolean disableDrowning() { return this.disableDrowning; }
        public boolean disableOffHandSwap() { return this.disableOffHandSwap; }
        public boolean disableWeatherChange() { return this.disableWeatherChange; }
        public boolean disableDeathMessage() { return this.disableDeathMessage; }
        public boolean disableMobSpawning() { return this.disableMobSpawning; }
        public boolean disableItemDrop() { return this.disableItemDrop; }
        public boolean disableItemPickup() { return this.disableItemPickup; }
        public boolean disableInventoryMovement() { return this.disableInventoryMovement; }
        public boolean disableBlockBreak() { return this.disableBlockBreak; }
        public boolean disableBlockPlace() { return this.disableBlockPlace; }
        public boolean disableBlockInteract() { return this.disableBlockInteract; }
        public boolean disableBlockBurn() { return this.disableBlockBurn; }
        public boolean disableBlockFireSpread() { return this.disableBlockFireSpread; }
        public boolean disableBlockLeafDecay() { return this.disableBlockLeafDecay; }
    }

    @ConfigSerializable
    public static class JoinLeaveMessages {
        @Comment("Should Akropolis handle join/quit messages?")
        private boolean enabled = true;

        @Comment("Set to '' if you want silent join/quit messages.")
        private String joinMessage = "<dark_gray>[<dark_green>+<dark_gray>] <yellow><player> <gray>connected!";
        private String quitMessage = "<dark_gray>[<dark_red>-<dark_gray>] <yellow><player> <gray>disconnected!";

        public boolean enabled() { return this.enabled; }
        public String joinMessage() { return this.joinMessage; }
        public String quitMessage() { return this.quitMessage; }
    }

    @ConfigSerializable
    public static class JoinSettings {
        @Comment("Change the hotbar slot focused on join (must be a number between 0-9, -1 to disable)")
        private int focusedSlot = 4;

        @Comment("Decide whether players will be hidden on join or not")
        private boolean playersHidden = false;

        @Comment("Should we teleport the player to the spawn point (if set) on join?")
        private boolean spawnJoin = true;

        @Comment("Should we heal the player?")
        private boolean heal = true;

        @Comment("Should we extinguish the player?")
        private boolean extinguish = true;

        @Comment("Should we clear their inventory?")
        private boolean clearInventory = false;

        @Comment("Spawn a firework on join.")
        private Firework firework = new Firework();


        public int focusedSlot() { return this.focusedSlot; }
        public boolean playersHidden() { return this.playersHidden; }
        public boolean spawnJoin() { return this.spawnJoin; }
        public boolean heal() { return this.heal; }
        public boolean extinguish() { return this.extinguish; }
        public boolean clearInventory() { return this.clearInventory; }
        public Firework firework() { return this.firework; }

        @ConfigSerializable
        public static class Firework {
            @Comment("Should we send a firework on join?")
            private boolean enabled = true;

            @Comment("Should we only send the firework on their first join?")
            private boolean firstJoinOnly = true;

            @Comment("""
                    You can watch examples of the types here: https://minecraft.wiki/w/Firework_Star#Shape_effects
                    Available types: BALL, BALL_LARGE, STAR, BURST, CREEPER\
                    """)
            private String type = "BALL_LARGE";

            @Comment("""
                    The greater the power, the longer the firework will fly.
                    Must be a number in the interval [0, 255].\
                    """)
            private int power = 1;

            @Comment("You can watch examples of the effects here: https://minecraft.wiki/w/Firework_Star#Additional_effects")
            private boolean flicker = true;
            private boolean trail = true;

            @Comment("You can find all available colors here: https://jd.papermc.io/paper/1.21.11/org/bukkit/Color.html#field-summary")
            private List<String> colors = List.of(
                    "AQUA",
                    "RED",
                    "TEAL",
                    "WHITE"
            );


            public boolean enabled() { return this.enabled; }
            public boolean firstJoinOnly() { return this.firstJoinOnly; }
            public String type() { return this.type; }
            public int power() { return this.power; }
            public boolean flicker() { return this.flicker; }
            public boolean trail() { return this.trail; }
            public List<String> colors() { return this.colors; }
        }
    }

    @ConfigSerializable
    public static class CustomJoinItems {
        @Comment("Should custom join items be enabled?")
        private boolean enabled = true;

        @Comment("Should we prevent them from moving/dropping the custom items?")
        private boolean disableInventoryMovement = true;

        @Comment("Add any items you want following the same format, more info in the wiki.")
        private Map<String, ItemRecord> items = MapUtils.linkedHashMapOfEntries(
                Map.entry("infobook", new ItemRecord(
                        "BOOK",
                        1,
                        0,
                        null,
                        null,
                        "<gold>Server Information <gray>(Right-Click)",
                        List.of(
                                "<dark_gray>» <gray>Right click to see information about the server!"
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of(
                                "[MESSAGE] <reset>",
                                "[MESSAGE] <dark_gray>| <gold><b>Server Information",
                                "[MESSAGE] <reset>",
                                "[MESSAGE] <dark_gray>» <gray>Website<dark_gray>: <yellow>www.example.com",
                                "[MESSAGE] <dark_gray>» <gray>Store<dark_gray>: <yellow>buy.example.com",
                                "[MESSAGE] <dark_gray>» <gray>Discord<dark_gray>: <yellow>discord.example.com",
                                "[MESSAGE] <reset>"
                        ),
                        null,
                        null,
                        null,
                        null
                )),
                // Should we use snake case here? Yes
                Map.entry("server_selector", new ItemRecord(
                        "NETHER_STAR",
                        1,
                        4,
                        null,
                        null,
                        "<aqua>Server Selector <gray>(Right-Click)",
                        List.of(
                                "<dark_gray>» <gray>Right click to open the server selector!"
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of(
                                "[MENU] serverselector"
                        ),
                        3,
                        null,
                        null,
                        null
                ))
        );


        public boolean enabled() { return this.enabled; }
        public boolean disableInventoryMovement() { return this.disableInventoryMovement; }
        public Map<String, ItemRecord> items() { return this.items; }
    }

    @ConfigSerializable
    public static class FightMode {
        @Comment("Should the fight mode feature be enabled?")
        private boolean enabled = true;

        @Comment("Slot the item should be given to?")
        private int slot = 7;

        @Comment("Should we prevent them from moving/dropping the item?")
        private boolean disableInventoryMovement = true;

        @Comment("Delay in seconds before the fight mode gets toggled.")
        private HoldDelay holdDelay = new HoldDelay();

        @Comment("The item that will toggle the fight mode and also the sword used to fight.")
        private ItemRecord item = new ItemRecord(
                "NETHERITE_SWORD",
                1,
                null,
                true,
                null,
                "<red>Fight Mode <gray>(Hold to toggle)",
                List.of(
                        "<dark_gray>» <gray>Hold for 5 seconds to toggle the fight mode!"
                ),
                null,
                List.of(
                        "HIDE_UNBREAKABLE",
                        "HIDE_ATTRIBUTES",
                        "HIDE_ENCHANTS"
                ),
                null,
                null,
                List.of(
                        "sharpness:5"
                ),
                null,
                null,
                null,
                null,
                null,
                null
        );

        @Comment("The armor that will be given to the player when they enter fight mode.")
        private Armor armor = new Armor();

        @Comment("Actions to execute when the fight mode gets activated or deactivated.")
        private Actions actions = new Actions();


        public boolean enabled() { return this.enabled; }
        public int slot() { return this.slot; }
        public boolean disableInventoryMovement() { return this.disableInventoryMovement; }
        public HoldDelay holdDelay() { return this.holdDelay; }
        public ItemRecord item() { return this.item; }
        public Armor armor() { return this.armor; }
        public Actions actions() { return this.actions; }


        @ConfigSerializable
        public static class Actions {
            private List<String> countdown = List.of(
                    "[SOUND] BLOCK_AMETHYST_BLOCK_PLACE"
            );
            private List<String> activated = List.of(
                    "[TITLE] <red><b>PvP activated!;<white>Try to survive!",
                    "[SOUND] ENTITY_ENDER_DRAGON_GROWL"
            );
            private List<String> deactivated = List.of(
                    "[TITLE] <green><b>PvP deactivated;<white>You've survived, uh?",
                    "[SOUND] ENTITY_SNIFFER_HAPPY"
            );

            public List<String> countdown() { return this.countdown; }
            public List<String> activated() { return this.activated; }
            public List<String> deactivated() { return this.deactivated; }
        }

        @ConfigSerializable
        public static class Armor {
            private ItemRecord helmet = new ItemRecord(
                    "DIAMOND_HELMET",
                    1,
                    null,
                    true,
                    null,
                    null,
                    null,
                    null,
                    List.of("HIDE_UNBREAKABLE"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            private ItemRecord chestplate = new ItemRecord(
                    "DIAMOND_CHESTPLATE",
                    1,
                    null,
                    true,
                    null,
                    null,
                    null,
                    null,
                    List.of("HIDE_UNBREAKABLE"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            private ItemRecord leggings = new ItemRecord(
                    "DIAMOND_LEGGINGS",
                    1,
                    null,
                    true,
                    null,
                    null,
                    null,
                    null,
                    List.of("HIDE_UNBREAKABLE"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            private ItemRecord boots = new ItemRecord(
                    "DIAMOND_BOOTS",
                    1,
                    null,
                    true,
                    null,
                    null,
                    null,
                    null,
                    List.of("HIDE_UNBREAKABLE"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            public ItemRecord helmet() { return this.helmet; }
            public ItemRecord chestplate() { return this.chestplate; }
            public ItemRecord leggings() { return this.leggings; }
            public ItemRecord boots() { return this.boots; }
        }

        @ConfigSerializable
        public static class HoldDelay {
            @Comment("The sword should be held for this amount of time to activate the fight mode.")
            private int activate = 5;

            @Comment("Any other item that isn't the sword should be held for this amount of time to deactivate the fight mode.")
            private int deactivate = 5;

            public int activate() { return this.activate; }
            public int deactivate() { return this.deactivate; }
        }
    }

    @ConfigSerializable
    public static class PlayerHider {
        @Comment("Should the player hider feature be enabled?")
        private boolean enabled = true;

        @Comment("Slot the item should be given to?")
        private int slot = 8;

        @Comment("Should we prevent them from moving/dropping the item?")
        private boolean disableInventoryMovement = true;

        @Comment("""
                Cooldown in seconds
                Set to 0 to disable\
                """)
        private int cooldown = 3;

        private ItemRecord notHidden = new ItemRecord(
                "LIME_DYE",
                1,
                null,
                null,
                null,
                "<gray>Players<dark_gray>: <green>Shown <gray>(Right-Click)",
                List.of(
                        "<dark_gray>» <gray>Click to hide all players!"
                ),
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
        );

        private ItemRecord hidden = new ItemRecord(
                "GRAY_DYE",
                1,
                null,
                null,
                null,
                "<gray>Players<dark_gray>: <red>Hidden <gray>(Right-Click)",
                List.of(
                        "<dark_gray>» <gray>Click to show all players!"
                ),
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
        );


        public boolean enabled() { return this.enabled; }
        public int slot() { return this.slot; }
        public boolean disableInventoryMovement() { return this.disableInventoryMovement; }
        public int cooldown() { return this.cooldown; }
        public ItemRecord notHidden() { return this.notHidden; }
        public ItemRecord hidden() { return this.hidden; }
    }
}
