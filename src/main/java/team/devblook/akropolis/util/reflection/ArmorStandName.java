package team.devblook.akropolis.util.reflection;

import com.cryptomorin.xseries.ReflectionUtils;
import org.bukkit.entity.ArmorStand;

public class ArmorStandName {

    private ArmorStandName() {
        throw new UnsupportedOperationException();
    }

    public static String getName(ArmorStand stand) {
        if (ReflectionUtils.supports(9))
            return stand.getCustomName();

        String name = null;
        try {
            name = (String) ArmorStand.class.getMethod("getCustomName").invoke(stand);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

}
