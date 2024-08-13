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

package team.devblook.akropolis.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import team.devblook.akropolis.util.TextUtil;

import java.util.List;

@SuppressWarnings("NullableProblems")
public abstract class InjectableCommand extends Command implements PluginIdentifiableCommand {
    private final Plugin plugin;

    protected InjectableCommand(Plugin plugin, String name, String description, List<String> aliases) {
        super(name, description, "/" + name, aliases);
        this.plugin = plugin;
    }

    protected InjectableCommand(Plugin plugin, String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            if (!isRegistered()) return true;
            onCommand(sender, label, args);
        } catch (Exception e) {
            sender.sendMessage(TextUtil.parse("<red>An error occurred processing this command. Please make sure your parameters are correct."));
            e.printStackTrace();
        }

        return true;
    }

    protected abstract void onCommand(CommandSender sender, String label, String[] args);

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}
