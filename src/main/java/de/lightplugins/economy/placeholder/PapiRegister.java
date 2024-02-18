package de.lightplugins.economy.placeholder;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.BankLevelSystem;
import de.lightplugins.economy.utils.Sorter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PapiRegister extends PlaceholderExpansion {

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
        return "5.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    /**
     * A description of the entire Java function.
     *
     * @param  player   OfflinePlayer object representing the player
     * @param  params   String containing parameters for the function
     * @return          String containing the result of the function
     *
     */
    @Override
    public String onRequest(OfflinePlayer player, String params) {

        FileConfiguration settings = Main.settings.getConfig();

        if(params.contains("moneytop_")) {

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

                            String message = Main.colorTranslation.hexTranslation(
                                    settings.getString("settings.top-placeholder-format"));

                            return message
                                    .replace("#number#", String.valueOf(i + 1))
                                    .replace("#name#", name)
                                    .replace("#amount#", String.valueOf(Main.util.finalFormatDouble(top.getValue())))
                                    .replace("#currency#", Main.util.getCurrency(top.getValue()));

                        }
                    } catch (Exception e) {
                        // Catch Exception for Map.Entry Exception if its null!
                        // e.printStackTrace();
                        String empty = Main.settings.getConfig().getString("settings.top-placeholder-not-set");
                        String message = Main.colorTranslation.hexTranslation(
                                settings.getString("settings.top-placeholder-format"));
                        return message
                                .replace("#number#", String.valueOf(i + 1))
                                .replace("#name#", empty != null ? empty : "-")
                                .replace("#amount#", "0.00")
                                .replace("#currency#", Main.util.getCurrency(0.00));
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        if(params.contains("moneytopname_")) {

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

                        if(params.equalsIgnoreCase("moneytopname_" + (i + 1))) {

                            return name;

                        }
                    } catch (Exception e) {
                        String empty = Main.settings.getConfig().getString("settings.top-placeholder-not-set");
                        return empty != null ? empty : "-";
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        if(params.contains("moneytopvalue_")) {

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

                        if(params.equalsIgnoreCase("moneytopvalue_" + (i + 1))) {

                            return String.valueOf(Main.util.fixDouble(top.getValue()));

                        }
                    } catch (Exception e) {
                        return "0";
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        if(params.contains("banktop_")) {

            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);

            List<String> exclude = new ArrayList<>(Main.settings.getConfig().getStringList("settings.baltop-exclude"));
            CompletableFuture<HashMap<String, Double>> futureMap = bankTableAsync.getPlayersBalanceList();
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

                        if(params.equalsIgnoreCase("banktop_" + (i + 1))) {

                            String message = Main.colorTranslation.hexTranslation(
                                    settings.getString("settings.top-placeholder-format"));

                            return message
                                    .replace("#number#", String.valueOf(i + 1))
                                    .replace("#name#", name)
                                    .replace("#amount#", String.valueOf(Main.util.finalFormatDouble(top.getValue())))
                                    .replace("#currency#", Main.util.getCurrency(top.getValue()));

                        }
                    } catch (Exception e) {
                        // Catch Exception for Map.Entry Exception if its null!
                        // e.printStackTrace();
                        String empty = Main.settings.getConfig().getString("settings.top-placeholder-not-set");
                        String message = Main.colorTranslation.hexTranslation(
                                settings.getString("settings.top-placeholder-format"));
                        return message
                                .replace("#number#", String.valueOf(i + 1))
                                .replace("#name#", empty != null ? empty : "-")
                                .replace("#amount#", "0.00")
                                .replace("#currency#", Main.util.getCurrency(0.00));
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        if(params.contains("banktopname_")) {

            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);

            List<String> exclude = new ArrayList<>(Main.settings.getConfig().getStringList("settings.baltop-exclude"));
            CompletableFuture<HashMap<String, Double>> futureMap = bankTableAsync.getPlayersBalanceList();
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

                        if(params.equalsIgnoreCase("banktopname_" + (i + 1))) {

                            return name;

                        }
                    } catch (Exception e) {
                        String empty = Main.settings.getConfig().getString("settings.top-placeholder-not-set");
                        return empty != null ? empty : "-";
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        if(params.contains("banktopvalue_")) {

            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);

            List<String> exclude = new ArrayList<>(Main.settings.getConfig().getStringList("settings.baltop-exclude"));
            CompletableFuture<HashMap<String, Double>> futureMap = bankTableAsync.getPlayersBalanceList();
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

                        if(params.equalsIgnoreCase("banktopvalue_" + (i + 1))) {

                            return String.valueOf(Main.util.fixDouble(top.getValue()));

                        }
                    } catch (Exception e) {
                        return "0";
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }


        if(params.equalsIgnoreCase("money_raw")) {
            double amount = Main.util.fixDouble(Main.economyImplementer.getBalance(player.getName()));
            return String.valueOf(amount);
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
                return Main.util.finalFormatDouble(completableFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        if(params.equalsIgnoreCase("bank_balance_raw")) {
            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);
            CompletableFuture<Double> completableFuture = bankTableAsync.playerBankBalance(player.getName());

            try {
                return String.valueOf(Main.util.fixDouble(completableFuture.get()));
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

        if(params.equalsIgnoreCase("money_short")) {
            double amount = Main.util.fixDouble(Main.economyImplementer.getBalance(player.getName()));

            String formatting = "undefined";
            double formattedAmount = 0;

            if(amount > 999.99) {
                formatting = "k";
                formattedAmount = amount / 1000.00;
            }

            if(amount > 999999.99) {
                formatting = "m";
                formattedAmount = amount / 1000000.00;
            }

            if(amount > 999999999.99) {
                formatting = "b";
                formattedAmount = amount / 1000000000.00;
            }

            if(amount > 999999999999.99) {
                formatting = "t";
                formattedAmount = amount / 1000000000000.00;
            }

            if(amount < 1000) {
                return Main.util.finalFormatDouble(amount);
            }

            String configFormatting = settings.getString("settings.shortPlaceholderFormat");

            if(configFormatting == null) {
                return "settings.yml error";
            }

            return Main.colorTranslation.hexTranslation(configFormatting
                    .replace("#amount#", Main.util.finalFormatDouble(formattedAmount))
                    .replace("#identifier#", formatting)
                    .replace("#currency#", Main.util.getCurrency(formattedAmount)));

        }

        if(params.equalsIgnoreCase("bank_balance_short")) {
            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);
            CompletableFuture<Double> completableFuture = bankTableAsync.playerBankBalance(player.getName());

            try {
                double amount = completableFuture.get();
                String formatting = "undefined";
                double formattedAmount = 0;

                if(amount > 999.99) {
                    formatting = "k";
                    formattedAmount = amount / 1000.00;
                }

                if(amount > 999999.99) {
                    formatting = "m";
                    formattedAmount = amount / 1000000.00;
                }

                if(amount > 999999999.99) {
                    formatting = "b";
                    formattedAmount = amount / 1000000000.00;
                }

                if(amount > 999999999999.99) {
                    formatting = "t";
                    formattedAmount = amount / 1000000000000.00;
                }

                if(amount < 1000) {
                    return Main.util.finalFormatDouble(amount);
                }

                String configFormatting = settings.getString("settings.shortPlaceholderFormat");

                if(configFormatting == null) {
                    return "settings.yml error";
                }

                return Main.colorTranslation.hexTranslation("test" + configFormatting
                        .replace("#amount#", Main.util.finalFormatDouble(formattedAmount))
                        .replace("#identifier#", formatting)
                        .replace("#currency#", Main.util.getCurrency(formattedAmount)));

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        if(params.equalsIgnoreCase("money_all")) {
            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);
            MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
            CompletableFuture<Double> completableFuture = bankTableAsync.playerBankBalance(player.getName());
            CompletableFuture<Double> completableFuture1 = moneyTableAsync.playerBalance(player.getName());

            try {
                double allTheMoney = completableFuture.get() + completableFuture1.get();
                return Main.util.finalFormatDouble(allTheMoney);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }


        return null; // Placeholder is unknown by the Expansion
    }
}
