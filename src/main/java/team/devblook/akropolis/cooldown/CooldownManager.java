/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2024 DevBlook Team and others
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

package team.devblook.akropolis.cooldown;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.UUID;

public class CooldownManager {
    private final Table<String, CooldownType, Long> cooldowns = HashBasedTable.create();

    /**
     * Retrieve the number of milliseconds left until a given cooldown expires.
     * <p>
     * Check for a negative value to determine if a given cooldown has expired. <br>
     * Cooldowns that have never been defined will return {@link Long#MIN_VALUE}.
     *
     * @param uuid - the uuid of the player.
     * @param key  - cooldown to locate.
     * @return Number of milliseconds until the cooldown expires.
     */
    public long getCooldown(UUID uuid, CooldownType key) {
        return calculateRemainder(cooldowns.get(uuid.toString(), key));
    }

    /**
     * Update a cooldown for the specified player.
     *
     * @param uuid  - uuid of the player.
     * @param key   - cooldown to update.
     * @param delay - number of milliseconds until the cooldown will expire again.
     */
    public void setCooldown(UUID uuid, CooldownType key, long delay) {
        calculateRemainder(cooldowns.put(uuid.toString(), key, System.currentTimeMillis() + (delay * 1000)));
    }

    /**
     * Determine if a given cooldown has expired. If it has, refresh the cooldown.
     * If not, do nothing.
     *
     * @param uuid  - uuid of the player.
     * @param key   - cooldown to update.
     * @param delay - number of milliseconds until the cooldown will expire again.
     * @return TRUE if the cooldown was expired/unset and has now been reset, FALSE
     * otherwise.
     */
    public boolean tryCooldown(UUID uuid, CooldownType key, long delay) {
        if (getCooldown(uuid, key) / 1000 > 0)
            return false;
        setCooldown(uuid, key, delay + 1);
        return true;
    }

    private long calculateRemainder(Long expireTime) {
        return expireTime != null ? expireTime - System.currentTimeMillis() : Long.MIN_VALUE;
    }
}