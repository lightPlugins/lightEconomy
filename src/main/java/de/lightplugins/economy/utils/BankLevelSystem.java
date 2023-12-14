package de.lightplugins.economy.utils;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BankLevelSystem {
    public Main plugin;
    public BankLevelSystem(Main plugin) {
        this.plugin = plugin;
    }


    private int getCurrentBankLevel(UUID owner) {

        BankTableAsync bankTable = new BankTableAsync(plugin);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);

        CompletableFuture<Integer> completableFuture = bankTable.playerCurrentBankLevel(offlinePlayer.getName());

        int currentLevel;

        try {
            currentLevel = completableFuture.get();
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return currentLevel;
    }

    private int getCurrentBankLevelString(String owner) {

        BankTableAsync bankTable = new BankTableAsync(plugin);

        CompletableFuture<Integer> completableFuture = bankTable.playerCurrentBankLevel(owner);

        int currentLevel;

        try {

            if(completableFuture.get() == null) {
                return 0;
            }

            currentLevel = completableFuture.get();
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return currentLevel;
    }

    public double getLimitByLevel(UUID owner) {

        int level = getCurrentBankLevel(owner);

        FileConfiguration levels = Main.bankLevelMenu.getConfig();

        for(String value : Objects.requireNonNull(levels.getConfigurationSection("levels")).getKeys(false))  {

            if(level == levels.getInt("levels." + value + ".level")) {
                return levels.getDouble("levels." + value + ".max-value");
            }
        }

        return 0.1;
    }

    public double getLimitByLevelString(String owner) {

        int level = getCurrentBankLevelString(owner);

        FileConfiguration levels = Main.bankLevelMenu.getConfig();

        for(String value : Objects.requireNonNull(levels.getConfigurationSection("levels")).getKeys(false))  {

            if(level == levels.getInt("levels." + value + ".level")) {
                return levels.getDouble("levels." + value + ".max-value");
            }
        }

        return 0.1;
    }
}
