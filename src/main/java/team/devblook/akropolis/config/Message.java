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

package team.devblook.akropolis.config;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import team.devblook.akropolis.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public enum Message {
    PREFIX("GENERAL.PREFIX"), USAGE("GENERAL.USAGE"), NO_PERMISSION("GENERAL.NO_PERMISSION"),
    CONSOLE_NOT_ALLOWED("GENERAL.CONSOLE_NOT_ALLOWED"),
    CUSTOM_COMMAND_NO_PERMISSION("GENERAL.CUSTOM_COMMAND_NO_PERMISSION"), INVALID_PLAYER("GENERAL.INVALID_PLAYER"),
    CONFIG_RELOAD("GENERAL.CONFIG_RELOAD"), COOLDOWN_ACTIVE("GENERAL.COOLDOWN_ACTIVE"),

    HELP_PLUGIN("HELP.PLUGIN"), HELP_HOLOGRAM("HELP.HOLOGRAM"),

    GAMEMODE_CHANGE("GAMEMODE.GAMEMODE_CHANGE"), GAMEMODE_CHANGE_OTHER("GAMEMODE.GAMEMODE_CHANGE_OTHER"),
    GAMEMODE_INVALID("GAMEMODE.GAMEMODE_INVALID"),

    VANISH_ENABLE("VANISH.ENABLE"), VANISH_DISABLE("VANISH.DISABLE"),

    FLIGHT_ENABLE("FLIGHT.ENABLE"), FLIGHT_ENABLE_OTHER("FLIGHT.ENABLE_OTHER"), FLIGHT_DISABLE("FLIGHT.DISABLE"),
    FLIGHT_DISABLE_OTHER("FLIGHT.DISABLE_OTHER"),

    PLAYER_HIDER_HIDDEN("PLAYER_HIDER.HIDDEN"), PLAYER_HIDER_SHOWN("PLAYER_HIDER.SHOWN"),

    SET_LOBBY("LOBBY.SET_LOBBY"),

    CLEARCHAT("CHAT.CLEARCHAT"), CLEARCHAT_PLAYER("CHAT.CLEARCHAT_PLAYER"), CHAT_LOCKED("CHAT.LOCKED"),
    CHAT_LOCKED_BROADCAST("CHAT.LOCKED_BROADCAST"), CHAT_UNLOCKED_BROADCAST("CHAT.UNLOCKED_BROADCAST"),
    ANTI_SWEAR_WORD_BLOCKED("CHAT.ANTI_SWEAR_WORD_BLOCKED"), ANTI_SWEAR_ADMIN_NOTIFY("CHAT.ANTI_SWEAR_ADMIN_NOTIFY"),
    COMMAND_BLOCKED("CHAT.COMMAND_BLOCKED"),

    SCOREBOARD_ENABLE("SCOREBOARD.ENABLE"), SCOREBOARD_DISABLE("SCOREBOARD.DISABLE"),

    DOUBLE_JUMP_COOLDOWN("DOUBLE_JUMP.COOLDOWN_ACTIVE"),

    EVENT_ITEM_DROP("WORLD_EVENT_MODIFICATIONS.ITEM_DROP"), EVENT_ITEM_PICKUP("WORLD_EVENT_MODIFICATIONS.ITEM_PICKUP"),
    EVENT_BLOCK_PLACE("WORLD_EVENT_MODIFICATIONS.BLOCK_PLACE"),
    EVENT_BLOCK_BREAK("WORLD_EVENT_MODIFICATIONS.BLOCK_BREAK"),
    EVENT_BLOCK_INTERACT("WORLD_EVENT_MODIFICATIONS.BLOCK_INTERACT"),
    EVENT_PLAYER_PVP("WORLD_EVENT_MODIFICATIONS.PLAYER_PVP"),

    HOLOGRAMS_EMPTY("HOLOGRAMS.EMPTY"), HOLOGRAMS_ALREADY_EXISTS("HOLOGRAMS.ALREADY_EXISTS"),
    HOLOGRAMS_INVALID_HOLOGRAM("HOLOGRAMS.INVALID_HOLOGRAM"), HOLOGRAMS_INVALID_LINE("HOLOGRAMS.INVALID_LINE"),
    HOLOGRAMS_SPAWNED("HOLOGRAMS.SPAWNED"), HOLOGRAMS_DESPAWNED("HOLOGRAMS.DESPAWNED"),
    HOLOGRAMS_MOVED("HOLOGRAMS.MOVED"), HOLOGRAMS_LINE_SET("HOLOGRAMS.LINE_SET"),
    HOLOGRAMS_ADDED_LINE("HOLOGRAMS.ADDED_LINE"), HOLOGRAMS_REMOVED_LINE("HOLOGRAMS.REMOVED_LINE"),

    WORLD_DOWNLOAD_NOTIFY("ANTI_WORLD_DOWNLOADER.ADMIN_NOTIFY");

    private static FileConfiguration config;
    private final String path;

    Message(String path) {
        this.path = path;
    }

    static void setConfiguration(FileConfiguration c) {
        config = c;
    }

    public void sendFrom(Audience audience) {
        Component messageContent = toComponent();

        if (messageContent.equals(Component.empty())) return;

        audience.sendMessage(messageContent);
    }

    public void sendFromAsList(Audience audience) {
        List<Component> messageContent = toComponentList();

        if (messageContent.getFirst().equals(Component.empty())) return;

        messageContent.forEach(audience::sendMessage);
    }

    public void sendFromWithReplacement(Audience audience, String pattern, Component replacement) {
        Component messageContent = TextUtil.replace(toComponent(), pattern, replacement);

        if (messageContent.equals(Component.empty())) return;

        audience.sendMessage(messageContent);
    }

    public Component toComponent() {
        String message = config.getString("Messages." + this.path);

        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        String rawPrefix = config.getString("Messages." + PREFIX.getPath());
        Component prefix = TextUtil.parse(rawPrefix != null && !rawPrefix.isEmpty() ? rawPrefix : "");

        return TextUtil.parseAndReplace(message, "prefix", prefix);
    }

    public List<Component> toComponentList() {
        List<String> message = config.getStringList("Messages." + this.path);
        List<Component> componentMessage = new ArrayList<>();

        if (message.isEmpty()) {
            componentMessage.add(Component.empty());
            return componentMessage;
        }

        String rawPrefix = config.getString("Messages." + PREFIX.getPath());
        Component prefix = TextUtil.parse(rawPrefix != null && !rawPrefix.isEmpty() ? rawPrefix : "");

        message.forEach(m -> componentMessage.add(TextUtil.parseAndReplace(m, "prefix", prefix)));
        return componentMessage;
    }

    public String getPath() {
        return this.path;
    }
}
