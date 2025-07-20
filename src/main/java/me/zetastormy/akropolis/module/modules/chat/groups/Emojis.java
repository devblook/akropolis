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

package me.zetastormy.akropolis.module.modules.chat.groups;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;

public class Emojis {
    private final Map<String, Set<String>> emoticonToEmojis;
    private final Pattern emojiPattern;

    public Emojis(ConfigurationSection emojiSection) {
        if (emojiSection == null) {
            this.emoticonToEmojis = Map.of();
            this.emojiPattern = Pattern.compile("");
            return;
        }

        this.emoticonToEmojis = new HashMap<>();

        emojiSection.getKeys(false).forEach(key -> {
            Set<String> emoji = new HashSet<>(emojiSection.getStringList(key + ".emoji"));

            if (emoji.isEmpty()) {
                emoji = Set.of(emojiSection.getString(key + ".emoji"));
            }

            List<String> emoticons = emojiSection.getStringList(key + ".emoticon");

            if (emoticons.isEmpty()) {
                emoticons = List.of(emojiSection.getString(key + ".emoticon"));
            }

            for (String emoticon : emoticons) {
                emoticonToEmojis.put(emoticon, emoji);
            }
        });

        List<String> escapedEmoticons = emoticonToEmojis.keySet().stream()
            .sorted(Comparator.comparingInt(String::length).reversed())
            .map(Pattern::quote)
            .toList();

        this.emojiPattern = Pattern.compile(String.join("|", escapedEmoticons));
    }

    public String parse(String text) {
        Matcher matcher = emojiPattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String found = matcher.group();
            Set<String> emojiSet = emoticonToEmojis.get(found);

            if (emojiSet != null && !emojiSet.isEmpty()) {
                String replacement = getRandomEmoji(emojiSet);
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private String getRandomEmoji(Set<String> emojis) {
        int index = new Random().nextInt(emojis.size());
        return emojis.stream().skip(index).findFirst().orElse("");
    }
}
