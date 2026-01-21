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

package me.zetastormy.akropolis.util;

import me.zetastormy.akropolis.util.text.PlaceholderUtil;
import me.zetastormy.akropolis.util.text.TextUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class MessagingUtil {

    public MessagingUtil() {
        throw new UnsupportedOperationException();
    }

    public static void send(String message, Audience audience) {
        Component messageContent = toComponent(message);

        if (messageContent.equals(Component.empty())) return;

        audience.sendMessage(messageContent);
    }

    public static void send(Component component, Audience audience) {
        audience.sendMessage(component);
    }

    public static void sendAsList(List<String> messageList, Audience audience) {
        List<Component> messageContent = toComponentList(messageList);

        if (messageContent.getFirst().equals(Component.empty())) return;

        messageContent.forEach(audience::sendMessage);
    }

    public static void sendWithReplacement(String message, Audience audience, String pattern, Component replacement) {
        Component messageContent = TextUtil.replace(
                toComponent(message),
                pattern,
                replacement
        );

        if (messageContent.equals(Component.empty())) return;

        audience.sendMessage(messageContent);
    }

    public static void sendWithReplacement(
            String message,
            Audience audience,
            String patternOne, Component replacementOne,
            String patternTwo, Component replacementTwo
    ) {
        Component messageContent = TextUtil.replace(
                TextUtil.replace(toComponent(message), patternOne, replacementOne),
                patternTwo,
                replacementTwo
        );

        if (messageContent.equals(Component.empty())) return;

        audience.sendMessage(messageContent);
    }

    public static Component toComponent(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        return PlaceholderUtil.setPlaceholders(message);
    }

    public static List<Component> toComponentList(List<String> messageList) {
        List<Component> componentMessage = new ArrayList<>();

        if (messageList.isEmpty()) {
            componentMessage.add(Component.empty());
            return componentMessage;
        }

        messageList.forEach(m -> componentMessage.add(PlaceholderUtil.setPlaceholders(m)));
        return componentMessage;
    }
}
