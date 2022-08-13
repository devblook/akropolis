package team.devblook.deluxehub.hook;

import org.bukkit.Bukkit;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.hook.hooks.head.BaseHead;
import team.devblook.deluxehub.hook.hooks.head.DatabaseHead;
import team.devblook.deluxehub.util.PlaceholderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HooksManager {
    private final Map<String, PluginHook> hooks;

    public HooksManager(DeluxeHubPlugin plugin) {
        hooks = new HashMap<>();

        // Base64 head
        hooks.put("BASE64", new BaseHead());

        // PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hooks.put("PLACEHOLDER_API", null);
            PlaceholderUtil.setPapiState(true);
            plugin.getLogger().info("Hooked into PlaceholderAPI");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            hooks.put("HEAD_DATABASE", new DatabaseHead());
            plugin.getLogger().info("Hooked into HeadDatabase");
        }

        hooks.values().stream().filter(Objects::nonNull).forEach(pluginHook -> pluginHook.onEnable(plugin));
    }

    public boolean isHookEnabled(String id) {
        return hooks.containsKey(id);
    }

    public PluginHook getPluginHook(String id) {
        return hooks.get(id);
    }
}
