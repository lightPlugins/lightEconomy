package de.lightplugins.economy.placeholder;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.BankLevelSystem;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlaceholderAPI extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lighteconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "lightPlugins";
    }

    @Override
    public @NotNull String getVersion() {
        return "5.0.3";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if(params.equalsIgnoreCase("money")) {
            double amount = Main.util.fixDouble(Main.economyImplementer.getBalance(player.getName()));
            return Main.util.formatDouble(amount);
        }
        if(params.equalsIgnoreCase("currency_singular")) {
            return Main.economyImplementer.currencyNameSingular();
        }
        if(params.equalsIgnoreCase("currency_plural")) {
            return Main.economyImplementer.currencyNamePlural();
        }
        if(params.equalsIgnoreCase("bank_balance")) {
            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);
            CompletableFuture<Double> completableFuture = bankTableAsync.playerBankBalance(player.getName());

            try {
                return String.valueOf(completableFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        if(params.equalsIgnoreCase("bank_current_level")) {
            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);
            CompletableFuture<Integer> completableFuture = bankTableAsync.playerCurrentBankLevel(player.getName());

            try {
                return String.valueOf(completableFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        if(params.equalsIgnoreCase("bank_max_level")) {
            FileConfiguration config = Main.bankLevelMenu.getConfig();

            int maxLevelViaConfig = 0;

            for(int i = 0; i < config.getConfigurationSection("levels").getKeys(false).size(); i++) {
                maxLevelViaConfig ++;
            }
            return String.valueOf(maxLevelViaConfig);
        }
        if(params.equalsIgnoreCase("bank_limit_balance")) {
            BankLevelSystem bankLevelSystem = new BankLevelSystem(Main.getInstance);
            return String.valueOf(bankLevelSystem.getLimitByLevel(player.getUniqueId()));
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
