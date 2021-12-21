package fun.lewisdev.deluxehub.config;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigHandler {
    private final JavaPlugin plugin;
    private final String name;
    private final File file;
    private YamlFile configFile;

    public ConfigHandler(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name + ".yml";
        this.file = new File(plugin.getDataFolder(), this.name);
        this.configFile = new YamlFile(file);
    }

    public void saveDefaultConfig() {
        InputStream configResource = plugin.getResource(name);

        if (configResource == null) {
            plugin.getLogger().log(Level.SEVERE, "The embedded resource {0} could not be find!", name);
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if (!file.exists()) {
            plugin.saveResource(name, false);
            plugin.getLogger().log(Level.INFO, "Resource file {0} written sucessfully!", name);
        }

        FileConfiguration defaultConfig = YamlFile.loadConfiguration(new InputStreamReader(configResource), true);

        try {
            configFile.loadWithComments();

            boolean hasChanges = false;

            for (String defaultKey : defaultConfig.getKeys(true)) {
                if (!configFile.contains(defaultKey)) {
                    configFile.set(defaultKey, defaultConfig.get(defaultKey));
                    hasChanges = true;
                }
            }

            if (hasChanges) {
                save();
                configFile.loadWithComments();
            }
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("There was an error loading " + name);
            plugin.getLogger().severe("Please check for any obvious configuration mistakes");
            plugin.getLogger().severe("such as using tabs for spaces or forgetting to end quotes");
            plugin.getLogger().severe("before reporting to the developer. The plugin will now disable.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

    }

    public void save() {
        if (configFile == null || file == null) return;

        try {
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        configFile = YamlFile.loadConfiguration(file, true);
    }

    public FileConfiguration get() {
        return configFile;
    }
}
