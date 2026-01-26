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
            |      _    _                          _ _
            |     / \\  | | ___ __ ___  _ __   ___ | (_)___
            |    / _ \\ | |/ / '__/ _ \\| '_ \\ / _ \\| | / __|
            |   / ___ \\|   <| | | (_) | |_) | (_) | | \\__ \\
            |  /_/   \\_\\_|\\_\\_|  \\___/| .__/ \\___/|_|_|___/
            |                         |_|
            --------
            | Akropolis data file, DO NOT EDIT if you don't know what you are doing.\
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
