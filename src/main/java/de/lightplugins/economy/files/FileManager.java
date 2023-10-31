package de.lightplugins.economy.files;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

public class FileManager {

    /*
     *
     * Configuration-Manager by lightPlugins © 2023
     *
     */


    private final Main plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final String configName;

    public FileManager(Main plugin, String configName) {
        this.plugin = plugin;
        this.configName = configName;
        saveDefaultConfig(configName);

    }

    public void reloadConfig(String configName) {
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), configName);

        this.plugin.reloadConfig();

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(configName);
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if(this.dataConfig == null)
            reloadConfig(configName);

        return this.dataConfig;

    }

    public void saveConfig() {
        if(this.dataConfig == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    private void saveDefaultConfig(String configName) {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), this.configName);

        if (!this.configFile.exists()) {
            this.plugin.saveResource(configName, false);
        } else {
            // Merge the default config into the existing config
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(Objects.requireNonNull(this.plugin.getResource(configName))));
            FileConfiguration existingConfig = getConfig();

            Bukkit.getConsoleSender().sendMessage("§cTEST 1");
            for (String key : defaultConfig.getKeys(true)) {
                Bukkit.getConsoleSender().sendMessage("§cTEST 2 " + key);
                if (!existingConfig.contains(key)) {
                    Bukkit.getConsoleSender().sendMessage("§cTEST 3 " + key);
                    existingConfig.set(key, defaultConfig.get(key));

                }
            }
            saveConfig();
        }
    }
}
