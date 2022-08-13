package team.devblook.deluxehub.hook.hooks.head;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.hook.PluginHook;

public class DatabaseHead implements PluginHook, HeadHook, Listener {
    private DeluxeHubPlugin plugin;
    private HeadDatabaseAPI api;

    @Override
    public void onEnable(DeluxeHubPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        api = new HeadDatabaseAPI();
    }

    @Override
    public ItemStack getHead(String data) {
        return api.getItemHead(data);
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent event) {
        plugin.getInventoryManager().onEnable(plugin);
    }
}
