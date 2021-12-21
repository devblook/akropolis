package fun.lewisdev.deluxehub.utility;

import com.cryptomorin.xseries.XMaterial;
import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.hook.hooks.head.HeadHook;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemStackBuilder {
    private final ItemStack itemStack;
    private static final DeluxeHubPlugin PLUGIN = JavaPlugin.getPlugin(DeluxeHubPlugin.class);

    public ItemStackBuilder(ItemStack item) {
        this.itemStack = item;
    }

    public static ItemStackBuilder getItemStack(ConfigurationSection section, Player player) {
        ItemStack item = parseMaterial(section);
        ItemStackBuilder builder = new ItemStackBuilder(item);

        if (section.contains("amount")) {
            builder.withAmount(section.getInt("amount"));
        }

        String username = section.getString("username");

        if (username != null && section.contains("username") && player != null) {
            builder.setSkullOwner(username.replace("%player%", player.getName()));
        }

        if (section.contains("display_name")) {
            if (player != null)
                builder.withName(section.getString("display_name"), player);
            else
                builder.withName(section.getString("display_name"));
        }

        if (section.contains("lore")) {
            if (player != null)
                builder.withLore(section.getStringList("lore"), player);
            else
                builder.withLore(section.getStringList("lore"));
        }

        if (section.contains("glow") && section.getBoolean("glow")) {
            builder.withGlow();
        }

        if (section.contains("item_flags")) {
            List<ItemFlag> flags = new ArrayList<>();
            section.getStringList("item_flags").forEach(text -> {
                try {
                    ItemFlag flag = ItemFlag.valueOf(text);
                    flags.add(flag);
                } catch (IllegalArgumentException ignored) {
                    // Ignored.
                }
            });
            builder.withFlags(flags.toArray(new ItemFlag[0]));
        }

        return builder;
    }

    public static ItemStackBuilder getItemStack(ConfigurationSection section) {
        return getItemStack(section, null);
    }

    public static ItemStack parseMaterial(ConfigurationSection section) {
        String rawMaterial = section.getString("material");

        if (rawMaterial== null) {
            PLUGIN.getLogger().severe("Could not get material from configuration section!");
            return new ItemStack(Material.AIR);
        }

        Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(rawMaterial);
        ItemStack item = xmaterial.isPresent() ? xmaterial.get().parseItem() : new ItemStack(Material.AIR);

        if (item == null) {
            PLUGIN.getLogger().severe("Could not parse material for item!");
            return new ItemStack(Material.AIR);
        }

        if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
            if (section.contains("base64")) {
                item = ((HeadHook) PLUGIN.getHookManager().getPluginHook("BASE64"))
                        .getHead(section.getString("base64"));
            } else if (section.contains("hdb") && PLUGIN.getHookManager().isHookEnabled("HEAD_DATABASE")) {
                item = ((HeadHook) PLUGIN.getHookManager().getPluginHook("HEAD_DATABASE"))
                        .getHead(section.getString("hdb"));
            }
        }

        return item;
    }

    public void withAmount(int amount) {
        itemStack.setAmount(amount);
    }

    public void withFlags(ItemFlag... flags) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply item flags!");
            return;
        }

        itemMeta.addItemFlags(flags);
        itemStack.setItemMeta(itemMeta);
    }

    public void withName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply item name!");
            return;
        }

        itemMeta.setDisplayName(TextUtil.color(name));
        itemStack.setItemMeta(itemMeta);
    }

    public void withName(String name, Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply item name with placeholder!");
            return;
        }

        itemMeta.setDisplayName(PlaceholderUtil.setPlaceholders(name, player));
        itemStack.setItemMeta(itemMeta);
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder setSkullOwner(String owner) {
        try {
            SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

            if (itemMeta == null) {
                PLUGIN.getLogger().severe("Invalid item meta, could not set skull owner!");
                return new ItemStackBuilder(new ItemStack(Material.AIR));
            }

            itemMeta.setOwner(owner);
            itemStack.setItemMeta(itemMeta);
        } catch (ClassCastException expected) {
            // Expected.
        }

        return this;
    }

    public void withLore(List<String> lore, Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply lore with placeholders!");
            return;
        }

        List<String> coloredLore = new ArrayList<>();

        for (String line : lore) {
            line = PlaceholderUtil.setPlaceholders(line, player);
            coloredLore.add(TextUtil.color(line));
        }

        itemMeta.setLore(coloredLore);
        itemStack.setItemMeta(itemMeta);
    }

    public void withLore(List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply lore!");
            return;
        }

        List<String> coloredLore = new ArrayList<>();

        for (String line : lore) {
            coloredLore.add(TextUtil.color(line));
        }

        itemMeta.setLore(coloredLore);
        itemStack.setItemMeta(itemMeta);
    }

    public void withGlow() {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply glow!");
            return;
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
    }

    public ItemStack build() {
        return itemStack;
    }
}
