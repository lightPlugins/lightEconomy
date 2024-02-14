package de.lightplugins.economy.files;

import de.lightplugins.economy.bungeecord.Bungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Objects;

public class FileManagerBungee {

    private final Bungee plugin;
    private Configuration dataConfig = null;
    private File configFile = null;
    private final String configName;

    public FileManagerBungee(Bungee plugin, String configName) {
        this.plugin = plugin;
        this.configName = configName;
        saveDefaultConfig(configName);
    }

    public void reloadConfig(String configName) {
        if (this.configFile == null)
            this.configFile = new File(plugin.getDataFolder(), configName);

        try {
            this.dataConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream defaultStream = plugin.getResourceAsStream(configName);
        if (defaultStream != null) {
            Configuration defaultConfig =
                    YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(defaultStream));
            for (String key : defaultConfig.getKeys()) {
                if (!this.dataConfig.contains(key)) {
                    this.dataConfig.set(key, defaultConfig.get(key));
                }
            }
        }
    }

    public Configuration getConfig() {
        if (this.dataConfig == null)
            reloadConfig(configName);

        return this.dataConfig;
    }

    public void saveConfig() {
        if (this.dataConfig == null || this.configFile == null)
            return;

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.dataConfig, this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig(String configName) {
        if (this.configFile == null)
            this.configFile = new File(plugin.getDataFolder(), this.configName);

        if (!this.configFile.exists()) {
            try {
                System.err.println("Die Ressource '" + configName + "' wurde nicht gefunden! + " + configFile.getPath());

                // Erstellen Sie das Verzeichnis, falls es nicht existiert
                Files.createDirectories(plugin.getDataFolder().toPath());
                Bungee.getInstance.firstStart = true;

                // Kopieren Sie die Datei
                String absolutePath = new File(plugin.getDataFolder(), configName).getAbsolutePath();
                Files.copy(Objects.requireNonNull(plugin.getResourceAsStream(configName)), new File(absolutePath).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Laden der Originalkonfiguration aus der Ressource
            Configuration defaultConfig = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new InputStreamReader(Objects.requireNonNull(plugin.getResourceAsStream(configName))));


            // Laden der vorhandenen Konfiguration
            Configuration existingConfig;
            try {
                existingConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            boolean changesMade = false;

            // Überprüfen jeder Einstellung in der Originalkonfiguration
            for (String key : defaultConfig.getKeys()) {
                // Überprüfen, ob die Einstellung in der vorhandenen Konfiguration nicht existiert
                if (!existingConfig.contains(key)) {
                    // Einstellung aus der Originalkonfiguration hinzufügen
                    existingConfig.set(key, defaultConfig.get(key));
                    System.out.println(Bungee.consolePrefix +
                            "Found §cnon existing config key§r. Adding §c" + key + " §rinto §c" + configName);
                    changesMade = true;
                } else {
                    /*
                    // Überprüfen, ob der Wert der Einstellung geändert wurde
                    if (!existingConfig.get(key).equals(defaultConfig.get(key))) {
                        // Wert der Einstellung in der vorhandenen Konfiguration aktualisieren
                        existingConfig.set(key, defaultConfig.get(key));
                        changesMade = true;
                    }

                     */
                }
            }

            // Wenn Änderungen vorgenommen wurden, die vorhandene Konfiguration speichern
            if (changesMade) {
                try {
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(existingConfig, configFile);
                    System.out.println(Bungee.consolePrefix + "Your config §c" + configName + " §rhas been updated.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(Bungee.consolePrefix + "Your config §c" + configName + " §ris up to date.");
            }
        }
    }
}
