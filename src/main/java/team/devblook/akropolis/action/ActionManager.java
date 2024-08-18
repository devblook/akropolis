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

package team.devblook.akropolis.action;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.actions.*;
import team.devblook.akropolis.util.PlaceholderUtil;
import team.devblook.akropolis.util.TextUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {
    private final AkropolisPlugin plugin;
    private final Map<String, Action> actions;

    public ActionManager(AkropolisPlugin plugin) {
        this.plugin = plugin;
        actions = new HashMap<>();
        load();
    }

    private void load() {
        registerAction(new MessageAction(), new BroadcastMessageAction(), new CommandAction(),
                new ConsoleCommandAction(), new SoundAction(), new PotionEffectAction(), new GamemodeAction(),
                new ServerAction(), new CloseInventoryAction(), new ActionbarAction(), new TitleAction(),
                new MenuAction());
    }

    public void registerAction(Action... actions) {
        Arrays.asList(actions).forEach(action -> this.actions.put(action.getIdentifier(), action));
    }

    public void executeActions(Player player, List<String> actions) {
        actions.forEach(actionContent -> {

            String actionName = StringUtils.substringBetween(actionContent, "[", "]").toUpperCase();
            Action action = actionName.isEmpty() ? null : this.actions.get(actionName);

            if (action != null) {
                actionContent = actionContent.contains(" ") ? actionContent.split(" ", 2)[1] : "";
                actionContent = TextUtil.raw(PlaceholderUtil.setPlaceholders(actionContent, player));

                action.execute(plugin, player, actionContent);
            }
        });
    }
}
