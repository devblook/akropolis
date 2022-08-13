package team.devblook.akropolis.hook.hooks.head;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.hook.PluginHook;

public class DatabaseHead implements PluginHook, HeadHook, Listener {
    private AkropolisPlugin plugin;
    private HeadDatabaseAPI api;

    @Override
    public void onEnable(AkropolisPlugin plugin) {
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
