/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2022 DevBlook Team and others
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

package team.devblook.akropolis.util;

import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.hook.hooks.head.HeadHook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemStackBuilder {
    private static final ItemStack MALFORMED_ITEM;
    private static final AkropolisPlugin PLUGIN;

    static {
        PLUGIN = AkropolisPlugin.getInstance();
        MALFORMED_ITEM = new ItemStack(Material.BARRIER);
        ItemMeta malformedMeta = MALFORMED_ITEM.getItemMeta();

        malformedMeta.displayName(TextUtil.parse("<red>Malformed item, check your config.yml!"));
        MALFORMED_ITEM.setItemMeta(malformedMeta);
    }

    private final ItemStack itemStack;

    public ItemStackBuilder(ItemStack item) {
        this.itemStack = item;
    }

    public static ItemStackBuilder getItemStack(ConfigurationSection section, Player player) {
        ItemStack item = parseMaterial(section);
        ItemStackBuilder builder = new ItemStackBuilder(item);

        if (item.getType().equals(Material.BARRIER))
            return new ItemStackBuilder(MALFORMED_ITEM);

        if (section.contains("amount")) {
            builder.withAmount(section.getInt("amount"));
        }

        String username = section.getString("username");

        if (username != null && section.contains("username") && player != null) {
            builder.setSkullOwner(username.replace("player", player.getName()));
        }

        if (section.contains("display_name")) {
            Component displayName = TextUtil.parse(section.getString("display_name"));

            if (player != null)
                builder.withName(displayName, player);
            else
                builder.withName(displayName);
        }

        if (section.contains("lore")) {
            List<Component> lore = new ArrayList<>();

            for (String line : section.getStringList("lore")) {
                lore.add(TextUtil.parse(line));
            }

            if (player != null)
                builder.withLore(lore, player);
            else
                builder.withLore(lore);
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
            return MALFORMED_ITEM;
        }

        Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(rawMaterial);

        if (xmaterial.isEmpty()) {
            PLUGIN.getLogger().severe("Could not parse material '" + rawMaterial + "'.");
            PLUGIN.getLogger().severe("Please check your config.yml!");
            return MALFORMED_ITEM;
        }

        ItemStack item = xmaterial.get().parseItem();

        if (item != null && item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
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
            PLUGIN.getLogger().severe("Please check your config.yml!");
            return;
        }

        itemMeta.addItemFlags(flags);
        itemStack.setItemMeta(itemMeta);
    }

    public void withName(Component name) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply item name!");
            PLUGIN.getLogger().severe("Please check your config.yml!");
            return;
        }

        name = name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        itemMeta.displayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public void withName(Component name, Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply item name with placeholder!");
            PLUGIN.getLogger().severe("Please check your config.yml!");
            return;
        }

        name = PlaceholderUtil.setPlaceholders(TextUtil.raw(name), player).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        itemMeta.displayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder setSkullOwner(String owner) {
        try {
            SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

            if (itemMeta == null) {
                PLUGIN.getLogger().severe("Invalid item meta, could not set skull owner!");
                PLUGIN.getLogger().severe("Please check your config.yml!");
                return new ItemStackBuilder(MALFORMED_ITEM);
            }

            itemMeta.setOwner(owner);
            itemStack.setItemMeta(itemMeta);
        } catch (ClassCastException expected) {
            // Expected.
        }

        return this;
    }

    public void withLore(List<Component> lore, Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply lore with placeholders!");
            PLUGIN.getLogger().severe("Please check your config.yml!");
            return;
        }

        List<Component> coloredLore = new ArrayList<>();

        for (Component line : lore) {
            line = PlaceholderUtil.setPlaceholders(TextUtil.raw(line), player);
            line = line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            coloredLore.add(line);
        }

        itemMeta.lore(coloredLore);
        itemStack.setItemMeta(itemMeta);
    }

    public void withLore(List<Component> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply lore!");
            PLUGIN.getLogger().severe("Please check your config.yml!");
            return;
        }

        List<Component> nonItalicLore = new ArrayList<>();

        for (Component line : lore) {
            line = line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            nonItalicLore.add(line);
        }

        itemMeta.lore(nonItalicLore);
        itemStack.setItemMeta(itemMeta);
    }

    public void withGlow() {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not apply glow!");
            PLUGIN.getLogger().severe("Please check your config.yml!");
            return;
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
    }

    public ItemStack build() {
        if (itemStack.getItemMeta() == null) {
            PLUGIN.getLogger().severe("Invalid item meta, could not build item.");
            PLUGIN.getLogger().severe("Please check your config.yml!");

            return MALFORMED_ITEM;
        }

        return itemStack;
    }
}
