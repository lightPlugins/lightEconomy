package de.lightplugins.economy.placeholder;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.BankLevelSystem;
import de.lightplugins.economy.utils.Sorter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

        List<String> exclude = new ArrayList<>(Main.settings.getConfig().getStringList("settings.baltop-exclude"));
        CompletableFuture<HashMap<String, Double>> futureMap = moneyTableAsync.getPlayersBalanceList();
        try {
            HashMap<String, Double> map = futureMap.get();
            for(String playername : exclude) {
                map.remove(playername);
            }
            TreeMap<String, Double> list = (new Sorter(map)).get();

            int baltopAmount = Main.settings.getConfig().getInt("settings.baltop-amount-of-players");

            for (int i = 0; i < baltopAmount; i++) {

                try {
                    Map.Entry<String, Double> top = list.pollFirstEntry();

                    String name = top.getKey();

                    if(params.equalsIgnoreCase("moneytop_" + (i + 1))) {


                        return MessagePath.MoneyTopFormat.getPath()
                                .replace("#number#", String.valueOf(i + 1))
                                .replace("#name#", name)
                                .replace("#amount#", String.valueOf(Main.util.finalFormatDouble(top.getValue())))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular());

                    }
                } catch (Exception e) {
                    // Catch Exception for Map.Entry Exception if its null!
                    // e.printStackTrace();
                    return MessagePath.MoneyTopFormat.getPath()
                            .replace("#number#", "x")
                            .replace("#name#", "Open")
                            .replace("#amount#", "0.00")
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular());
                }
            }



        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }


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
