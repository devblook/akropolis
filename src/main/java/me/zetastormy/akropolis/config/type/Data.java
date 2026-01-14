package me.zetastormy.akropolis.config.type;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Data {
    public static String HEADER = """
            Akropolis data file, DO NOT EDIT if you don't know what you are doing.\
            """;

    private boolean chatLocked = false;
    private Map<String, Hologram> holograms = Map.of();
    private @Nullable Location spawn = null;
    private Map<UUID, PlayerData> players = Map.of();
    private SongPlayer songPlayer = new SongPlayer();


    public boolean isChatLocked() { return this.chatLocked; }
    public void setChatLocked(boolean chatLocked) { this.chatLocked = chatLocked; }

    public Map<String, Hologram> getHolograms() { return this.holograms; }

    public @Nullable Location getSpawn() { return this.spawn; }
    public void setSpawn(@Nullable Location location) { this.spawn = location; }

    public Map<UUID, PlayerData> getPlayers() { return this.players; }

    public SongPlayer getSongPlayer() { return this.songPlayer; }

    @ConfigSerializable
    public static class SongPlayer {
        private @Nullable Location location = null;

        public @Nullable Location getLocation() { return this.location; }
        public void setLocation(@Nullable Location location) { this.location = location; }
    }

    @ConfigSerializable
    public static class PlayerData {
        private boolean fly = false;

        // Required by Configurate to load data
        public PlayerData() {}

        public PlayerData(boolean fly) {
            this.fly = fly;
        }

        public boolean getFly() { return this.fly; }
        public void setFly(boolean fly) { this.fly = fly; }
    }

    @ConfigSerializable
    public static class Hologram {
        private List<String> lines;
        private Location location;

        // Required by Configurate to load data
        public Hologram() {}

        public Hologram(List<String> lines, Location location) {
            this.lines = lines;
            this.location = location;
        }

        public List<String> getLines() { return this.lines; }
        public void setLines(List<String> lines) { this.lines = lines; }
        public Location getLocation() { return this.location; }
        public void setLocation(Location location) { this.location = location; }
    }
}
