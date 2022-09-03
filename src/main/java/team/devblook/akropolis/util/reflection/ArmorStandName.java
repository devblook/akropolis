package team.devblook.akropolis.util.reflection;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.ArmorStand;

public class ArmorStandName {

    private ArmorStandName() {
        throw new UnsupportedOperationException();
    }

    public static Component getName(ArmorStand stand) {
        return stand.customName();
    }
}
