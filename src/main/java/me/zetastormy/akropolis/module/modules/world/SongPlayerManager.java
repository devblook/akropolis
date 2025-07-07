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

package me.zetastormy.akropolis.module.modules.world;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.xxmicloxx.NoteBlockAPI.model.FadeType;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.Fade;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.config.ConfigHandler;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;

public class SongPlayerManager extends Module {
    private SongPlayer songPlayer;
    private ConfigHandler dataConfig;

    public SongPlayerManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.SONG_PLAYER);
    }

    @Override
    public void onEnable() {
        if (!getPlugin().getHookManager().isHookEnabled("NOTEBLOCKAPI")) {
            getPlugin().getLogger().warning("NoteBlockAPI is not enabled! The songs module won't be enabled.");
            return;
        }

        Playlist playlist = loadSongs();

        if (playlist == null) {
            getPlugin().getLogger().warning("Songs' directory is empty! The songs module won't be enabled.");
            return;
        }

        dataConfig = getPlugin().getConfigManager().getFile(ConfigType.DATA);

        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        String type = config.getString("song_player.type", "RADIO");
        int distance = config.getInt("song_player.distance", 16);

        int fadeInDuration = config.getInt("song_player.fade.in", 20);
        int fadeOutDuration = config.getInt("song_player.fade.out", 20);
        byte volume = (byte)config.getInt("song_player.volume", 85);

        songPlayer = createSongPlayer(playlist, type, distance);

        Bukkit.getScheduler()
            .runTaskLaterAsynchronously(getPlugin(), () -> Bukkit.getOnlinePlayers().stream()
                                                            .filter(player -> !inDisabledWorld(player.getLocation()))
                                                            .forEach(songPlayer::addPlayer), 20L);

        Fade fadeIn = songPlayer.getFadeIn();
        Fade fadeOut = songPlayer.getFadeOut();

        fadeIn.setType(FadeType.LINEAR);
        fadeOut.setType(FadeType.LINEAR);

        fadeIn.setFadeDuration(fadeInDuration);
        fadeOut.setFadeDuration(fadeOutDuration);

        songPlayer.setVolume(volume);
        songPlayer.setRepeatMode(RepeatMode.ALL);
        songPlayer.setPlaying(true);
    }

    @Override
    public void onDisable() {
        if (songPlayer != null) songPlayer.destroy();
    }

    private Playlist loadSongs() {
        File directory = new File(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "songs");

        if (!directory.exists()) {
            if (!directory.mkdir()) {
                getPlugin().getLogger().severe("Could not create songs' directory!");
                getPlugin().getLogger().severe("The plugin will now disable.");
                Bukkit.getPluginManager().disablePlugin(getPlugin());
                return null;
            }
        }

        File[] nbsFiles = new File(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "songs").listFiles(
            (dir, name) -> name.toLowerCase().endsWith(".nbs")
        );

        Set<Song> songs = new HashSet<>();

        for (File song : nbsFiles) {
            songs.add(NBSDecoder.parse(song));
        }

        if (songs.isEmpty()) return null;

        return new Playlist(songs.toArray(new Song[0]));
    }

    private SongPlayer createSongPlayer(Playlist playlist, String type, int distance) {
        if (type.equalsIgnoreCase("RADIO")) {
            return new RadioSongPlayer(playlist);
        }

        Location songLocation = dataConfig.get().getLocation("song_player.location");

        if (songLocation == null) {
            getPlugin().getLogger().warning("Couldn't get song player location! Using radio song player instead.");

            return new RadioSongPlayer(playlist);
        }

        PositionSongPlayer songPlayer = new PositionSongPlayer(playlist);

        songPlayer.setTargetLocation(songLocation);
        songPlayer.setDistance(distance);

        return songPlayer;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (songPlayer == null || inDisabledWorld(event.getPlayer().getLocation())) return;

        songPlayer.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (songPlayer == null || inDisabledWorld(event.getPlayer().getLocation())) return;

        songPlayer.removePlayer(event.getPlayer());
    }

    public void setLocation(Location location) {
        dataConfig.get().set("song_player.location", location);
        dataConfig.save();
    }

    public void skip() {
        if (songPlayer == null) return;

        songPlayer.playNextSong();
    }

    public String getCurrentSong() {
        if (songPlayer == null) return "";

        return songPlayer.getSong().getTitle();
    }

    public SongPlayer getSongPlayer() {
        return songPlayer;
    }
}
