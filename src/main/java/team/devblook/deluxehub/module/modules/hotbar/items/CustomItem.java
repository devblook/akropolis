package team.devblook.deluxehub.module.modules.hotbar.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.devblook.deluxehub.config.ConfigType;
import team.devblook.deluxehub.module.modules.hotbar.HotbarItem;
import team.devblook.deluxehub.module.modules.hotbar.HotbarManager;

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
