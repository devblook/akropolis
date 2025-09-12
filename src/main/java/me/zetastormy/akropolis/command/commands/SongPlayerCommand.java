package me.zetastormy.akropolis.command.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.command.InjectableCommand;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.modules.world.SongPlayerManager;
import me.zetastormy.akropolis.util.text.TextUtil;

public class SongPlayerCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public SongPlayerCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "songplayer", "Manage the song player", aliases);
        this.plugin = plugin;
    }

	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.CONSOLE_NOT_ALLOWED.send(sender);
            return;
        }

        if (!sender.hasPermission(Permissions.COMMAND_SONG_PLAYER.getPermission())) {
            Message.NO_PERMISSION.send(sender);
            return;
        }

        if (args.length == 1) {
            Message.HELP_SONG_PLAYER.toComponentList().forEach(sender::sendMessage);
            return;
        }

        SongPlayerManager songPlayerManager = plugin.getSongPlayerManager();

        if (songPlayerManager.getSongPlayer() == null) {
            Message.SONG_PLAYER_NOT_LOADED.send(sender);
            return;
        }

        if (args[1].equalsIgnoreCase("setpos")) {
            songPlayerManager.setLocation(player.getLocation());
            Message.SONG_PLAYER_SET_LOCATION.send(sender);
        }

        if (args[1].equalsIgnoreCase("skip")) {
            songPlayerManager.skip();

            // Delay it, because the change is not immediate in the song player.
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                Message.SONG_PLAYER_SKIPPED.sendWithReplacement(
                    sender,
                    "current_song",
                    TextUtil.parse(songPlayerManager.getCurrentSong()));
            }, 20L);
        }
	}
}
