package de.lightplugins.economy.utils;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

public class DebugPrinting {

    public void sendInfo(String message) {
        FileConfiguration fileConfiguration = Main.settings.getConfig();
        boolean debugMode = fileConfiguration.getBoolean("settings.debug");

        if(debugMode) {
            Bukkit.getLogger().log(Level.INFO, "[lightEconomy] " + message);
        }
    }

    public void sendWarning(String message) {
        FileConfiguration fileConfiguration = Main.settings.getConfig();
        boolean debugMode = fileConfiguration.getBoolean("settings.debug");

        if(debugMode) {
            Bukkit.getLogger().log(Level.WARNING, "[lightEconomy] " + message);
        }
    }

    public void sendError(String message) {
        FileConfiguration fileConfiguration = Main.settings.getConfig();
        boolean debugMode = fileConfiguration.getBoolean("settings.debug");

        if(debugMode) {
            Bukkit.getLogger().log(Level.SEVERE, "[lightEconomy] " + message);
        }
    }
}
