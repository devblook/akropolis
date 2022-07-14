package fun.lewisdev.deluxehub.action;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.actions.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {
    private final DeluxeHubPlugin plugin;
    private final Map<String, Action> actions;

    public ActionManager(DeluxeHubPlugin plugin) {
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

                action.execute(plugin, player, actionContent);
            }
        });
    }
}
