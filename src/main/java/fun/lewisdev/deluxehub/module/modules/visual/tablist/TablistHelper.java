package fun.lewisdev.deluxehub.module.modules.visual.tablist;

import com.cryptomorin.xseries.ReflectionUtils;
import com.google.common.base.Strings;
import fun.lewisdev.deluxehub.utility.TextUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class TablistHelper {

    private TablistHelper() {
        throw new UnsupportedOperationException();
    }

    public static void sendTabList(Player player, String header, String footer) {
        Objects.requireNonNull(player, "Cannot update tab for null player");
        header = Strings.isNullOrEmpty(header) ? ""
                : TextUtil.color(header).replace("%player%", player.getDisplayName());
        footer = Strings.isNullOrEmpty(footer) ? ""
                : TextUtil.color(footer).replace("%player%", player.getDisplayName());

        if (ReflectionUtils.supports(14)) {
            player.setPlayerListHeaderFooter(header, footer);
            return;
        }

        try {
            Class<?> chatNms = ReflectionUtils.getNMSClass("IChatBaseComponent");

            if (chatNms == null) return;

            Method chatComponentBuilderMethod = chatNms.getDeclaredClasses()[0].getMethod("a", String.class);
            Object tabHeader = chatComponentBuilderMethod.invoke(null, "{\"text\":\"" + header + "\"}");
            Object tabFooter = chatComponentBuilderMethod.invoke(null, "{\"text\":\"" + footer + "\"}");

            Class<?> packetNms = ReflectionUtils.getNMSClass("PacketPlayOutPlayerListHeaderFooter");

            if (packetNms == null) return;

            Object packet = packetNms.getConstructor().newInstance();

            setFields(tabHeader, tabFooter, packet);
            ReflectionUtils.sendPacket(player, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setFields(Object tabHeader, Object tabFooter, Object packet)
            throws IllegalAccessException, NoSuchFieldException {
        Field aField;
        Field bField;
        try {
            aField = packet.getClass().getDeclaredField("a");
            bField = packet.getClass().getDeclaredField("b");
        } catch (Exception ex) {
            aField = packet.getClass().getDeclaredField("header");
            bField = packet.getClass().getDeclaredField("footer");
        }

        aField.setAccessible(true);
        aField.set(packet, tabHeader);

        bField.setAccessible(true);
        bField.set(packet, tabFooter);
    }
}
