package team.devblook.akropolis.module.modules.hotbar.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.modules.hotbar.HotbarItem;
import team.devblook.akropolis.module.modules.hotbar.HotbarManager;

import java.util.List;

public class CustomItem extends HotbarItem {
    private final List<String> actions;

    public CustomItem(HotbarManager hotbarManager, ItemStack item, int slot, String key) {
        super(hotbarManager, item, slot, key);
        actions = getPlugin().getConfigManager().getFile(ConfigType.SETTINGS).get()
                .getStringList("custom_join_items.items." + key + ".actions");
    }

    @Override
    protected void onInteract(Player player) {
        getPlugin().getActionManager().executeActions(player, actions);
    }
}
