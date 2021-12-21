package fun.lewisdev.deluxehub.inventory.inventories;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.inventory.AbstractInventory;
import fun.lewisdev.deluxehub.inventory.InventoryBuilder;
import fun.lewisdev.deluxehub.inventory.InventoryItem;
import fun.lewisdev.deluxehub.utility.ItemStackBuilder;
import org.bukkit.inventory.Inventory;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.FileConfiguration;

public class CustomGUI extends AbstractInventory {
    private final FileConfiguration config;
    private final ConfigurationSection itemsSection;
    private InventoryBuilder inventory;

    public CustomGUI(DeluxeHubPlugin plugin, FileConfiguration config) {
        super(plugin);
        this.config = config;
        this.itemsSection = config.getConfigurationSection("items");
    }

    @Override
    public void onEnable() {
        InventoryBuilder inventoryBuilder = new InventoryBuilder(config.getInt("slots"),
                config.getString("title"));

        if (config.contains("refresh") && config.getBoolean("refresh.enabled")) {
            setInventoryRefresh(config.getLong("refresh.rate"));
        }

        if (itemsSection == null) {
            getPlugin().getLogger().severe("Items configuration section is missing!");
            return;
        }

        for (String item : itemsSection.getKeys(false)) {
            try {
                InventoryItem inventoryItem = build(item);
                setFiller(inventoryBuilder, item, inventoryItem);
            } catch (Exception e) {
                e.printStackTrace();
                getPlugin().getLogger().warning("There was an error loading GUI item ID '" + item + "', skipping..");
            }
        }

        inventory = inventoryBuilder;
    }

    private InventoryItem build(String item) {
        ItemStackBuilder itemStackBuilder = ItemStackBuilder
                .getItemStack(itemsSection.getConfigurationSection(item));
        InventoryItem inventoryItem;

        if (!itemsSection.contains(item + ".actions")) {
            inventoryItem = new InventoryItem(itemStackBuilder.build());
        } else {
            inventoryItem = new InventoryItem(itemStackBuilder.build()).addClickAction(p -> getPlugin()
                    .getActionManager().executeActions(p, itemsSection.getStringList(item + ".actions")));
        }

        return inventoryItem;
    }

    private void setFiller(InventoryBuilder inventoryBuilder, String item, InventoryItem inventoryItem) {
        if (itemsSection.contains(item + ".slots")) {
            for (String slot : itemsSection.getStringList(item + ".slots")) {
                inventoryBuilder.setItem(Integer.parseInt(slot), inventoryItem);
            }
        } else if (itemsSection.contains(item + ".slot")) {
            int slot = itemsSection.getInt(item + ".slot");

            if (slot == -1) {
                while (inventoryBuilder.getInventory().firstEmpty() != -1) {
                    inventoryBuilder.setItem(inventoryBuilder.getInventory().firstEmpty(), inventoryItem);
                }
            } else {
                inventoryBuilder.setItem(slot, inventoryItem);
            }
        }
    }

    @Override
    protected Inventory getInventory() {
        return inventory.getInventory();
    }
}
