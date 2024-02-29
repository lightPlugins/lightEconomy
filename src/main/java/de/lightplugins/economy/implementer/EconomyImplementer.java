package de.lightplugins.economy.implementer;

import de.lightplugins.economy.api.enums.TransactionStatus;
import de.lightplugins.economy.api.events.EconomyWithdrawPocketEvent;
import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.master.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;


    /*

        IMPORTANT: All database relevant info's in SYNC here !

     */

public class EconomyImplementer implements Economy {

    public Main plugin = Main.getInstance;


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        // Other plugins are not showing the currency
        // return Main.util.formatDouble(v)
        return Main.util.formatDouble(v) + " " + Main.util.getCurrency(v);
    }

    @Override
    public String currencyNamePlural() {

        FileConfiguration settings = Main.settings.getConfig();
        return settings.getString("settings.currency-name-plural");
    }

    @Override
    public String currencyNameSingular() {

        FileConfiguration settings = Main.settings.getConfig();
        return settings.getString("settings.currency-name-singular");
    }

    @Override
    public boolean hasAccount(String s) {

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        CompletableFuture<Double> balanceFuture = moneyTableAsync.playerBalance(s);

        try {
            Double balance = balanceFuture.get();
            return balance != null;

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer.getName());
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer.getName());
    }

    @Override
    public double getBalance(String s) {

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);
        CompletableFuture<Double> balance = moneyTableAsync.playerBalance(s);
        CompletableFuture<Boolean> isPlayer = moneyTableAsync.isPlayerAccount(s);

        FileConfiguration settings = Main.settings.getConfig();
        boolean bankAsPocket = settings.getBoolean("settings.bankAsPocket");

        try {

            if(bankAsPocket && isPlayer.get()) {
                CompletableFuture<Double> bankBalance = bankTableAsync.playerBankBalance(s);
                return balance.get() + bankBalance.get(); }

            return balance.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getName());
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer.getName());
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return has(offlinePlayer.getName(), v);
    }

    @Override
    public boolean has(String s, double v) {

        BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);
        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

        CompletableFuture<Double> bankBalance = bankTableAsync.playerBankBalance(s);
        CompletableFuture<Boolean> isPlayer = moneyTableAsync.isPlayerAccount(s);

        FileConfiguration settings = Main.settings.getConfig();
        boolean bankAsPocket = settings.getBoolean("settings.bankAsPocket");

        if(!bankAsPocket && getBalance(s) >= v) {
            return true;
        }

        try {

            double currentBankBalance = 0;

            if(isPlayer.get()) {
                currentBankBalance = bankBalance.get();
            }

            double missingAmount = v - getBalance(s);

            if(currentBankBalance >= missingAmount) { return true; }


        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Something went wrong due to bankAsPocket funtion", e);
        }

        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {

        EconomyWithdrawPocketEvent economyWithdrawPocketEvent = new EconomyWithdrawPocketEvent(s, v);
        Bukkit.getServer().getPluginManager().callEvent(economyWithdrawPocketEvent);

        v = economyWithdrawPocketEvent.getAmount();

        if(economyWithdrawPocketEvent.isCancelled()) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Event was cancelled by another plugin");
        }

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);

        CompletableFuture<Double> currentPocketBalance = moneyTableAsync.playerBalance(s);

        FileConfiguration titles = Main.titles.getConfig();

        double minTrigger = titles.getDouble("titles.withdraw-wallet.min-trigger-amount");

                /*
                    Title on count up
                 */

        String upperTitle = Main.colorTranslation.hexTranslation(
                titles.getString("titles.withdraw-wallet.counter.upper-line"));
        String lowerTitle = Main.colorTranslation.hexTranslation(
                titles.getString("titles.withdraw-wallet.counter.lower-line"));

                /*
                    Title on count finished
                 */

        String upperTitleFinal = Main.colorTranslation.hexTranslation(
                titles.getString("titles.withdraw-wallet.final.upper-line"));

        String lowerTitleFinal = Main.colorTranslation.hexTranslation(
                titles.getString("titles.withdraw-wallet.final.lower-line"));

        if(!hasAccount(s)) {
            economyWithdrawPocketEvent.setTransactionStatus(TransactionStatus.NO_ACCOUNT);
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The Player does not have an account");
        }

        if(!has(s, v)) {
            economyWithdrawPocketEvent.setTransactionStatus(TransactionStatus.NOT_ENOUGH_MONEY);
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The Player has not enough money");
        }

        if(v < 0.0) {
            economyWithdrawPocketEvent.setTransactionStatus(TransactionStatus.NEGATIVE_VALUE);
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Cant withdraw negative numbers");
        }

        try {

            double currentBalance = currentPocketBalance.get();

            if(v > currentBalance) {
                CompletableFuture<Double> bankBalance = bankTableAsync.playerBankBalance(s);

                double missingBalance = v - currentBalance;

                    double currentBankBalance = bankBalance.get();

                    if(currentBankBalance >= missingBalance) {

                        CompletableFuture<Boolean> withdrawFromBank =
                                bankTableAsync.setBankMoney(s, currentBankBalance - missingBalance);

                        CompletableFuture<Boolean> withdrawFromPocket =
                                moneyTableAsync.setMoney(s, 0.00);

                        if(withdrawFromBank.get() && withdrawFromPocket.get()) {



                            OfflinePlayer offlinePlayer = Bukkit.getPlayer(s);

                            if(offlinePlayer != null && offlinePlayer.isOnline()) {

                                Player player = offlinePlayer.getPlayer();

                                if(titles.getBoolean("titles.withdraw-wallet.enable")) {
                                    if(v >= minTrigger) {
                                        Main.util.countUp(player, v, upperTitle, lowerTitle, upperTitleFinal, lowerTitleFinal);
                                    }
                                }
                            }

                            economyWithdrawPocketEvent.setTransactionStatus(TransactionStatus.SUCCESS);

                            return new EconomyResponse(v, currentBalance, EconomyResponse.ResponseType.SUCCESS,
                                    "[lightEconomy] Successfully withdraw the missing money from lightEconomy bank");


                        }
                    }

                economyWithdrawPocketEvent.setTransactionStatus(TransactionStatus.FAILED);

                return new EconomyResponse(v, currentBalance, EconomyResponse.ResponseType.FAILURE,
                        "[lightEconomy] Something went wrong on withdraw with option bankAsPocket");
            }

            currentBalance -= v;

            CompletableFuture<Boolean> completableFuture = moneyTableAsync.setMoney(s, currentBalance);

            if(completableFuture.get()) {

                OfflinePlayer offlinePlayer = Bukkit.getPlayer(s);

                if(offlinePlayer != null && offlinePlayer.isOnline()) {

                    Player player = offlinePlayer.getPlayer();

                    if(titles.getBoolean("titles.withdraw-wallet.enable")) {
                        if(v >= minTrigger) {
                            Main.util.countUp(player, v, upperTitle, lowerTitle, upperTitleFinal, lowerTitleFinal);
                        }
                    }
                }


                return new EconomyResponse(v, currentBalance, EconomyResponse.ResponseType.SUCCESS,
                        "[lightEconomy] Successfully withdraw");
            }

            return new EconomyResponse(v, currentBalance, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Something went wrong on withdraw");

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Something went wrong due to bankAsPocket funtion", e);
        }

    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {

        /**
         *  TODO: not using of get() function due to freeze the main thread. Instead use .thenAccept()
         */

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        FileConfiguration settings = Main.settings.getConfig();
        double maxPocketBalance = settings.getDouble("settings.max-pocket-balance");

        if(!hasAccount(s)) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The Player does not have an account");
        }

        if(v < 0.0) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Cant deposit negative numbers");
        }

        if(v > maxPocketBalance) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The deposit value is to big");
        }


        CompletableFuture<Double> futureBalance = moneyTableAsync.playerBalance(s);

        try {

            double currentBalance = futureBalance.get();
            currentBalance += v;

            if(currentBalance > maxPocketBalance) {
                return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                        "[lightEconomy] The player reached the max balance for his pocket");
            }

            CompletableFuture<Boolean> completableFuture = moneyTableAsync.setMoney(s, currentBalance);

            if(completableFuture.get()) {

                FileConfiguration titles = Main.titles.getConfig();
                double minTrigger = titles.getDouble("titles.deposit-wallet.min-trigger-amount");

                /*
                    Title on count up
                 */

                String upperTitle = Main.colorTranslation.hexTranslation(
                                titles.getString("titles.deposit-wallet.counter.upper-line"));
                String lowerTitle = Main.colorTranslation.hexTranslation(
                                titles.getString("titles.deposit-wallet.counter.lower-line"));

                /*
                    Title on count finished
                 */

                String upperTitleFinal = Main.colorTranslation.hexTranslation(
                                titles.getString("titles.deposit-wallet.final.upper-line"));
                String lowerTitleFinal = Main.colorTranslation.hexTranslation(
                                titles.getString("titles.deposit-wallet.final.lower-line"));


                OfflinePlayer offlinePlayer = Bukkit.getPlayer(s);

                if(offlinePlayer != null && offlinePlayer.isOnline()) {

                    Player player = offlinePlayer.getPlayer();

                    if(titles.getBoolean("titles.deposit-wallet.enable")) {
                        if(v >= minTrigger) {
                            Main.util.countUp(player, v, upperTitle, lowerTitle, upperTitleFinal, lowerTitleFinal);
                        }
                    }
                }

                return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS,
                        "[lightEconomy] Successfully deposit");
            }
            return new EconomyResponse(v, currentBalance, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Something went wrong on deposit");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        CompletableFuture<Boolean> completableFuture = moneyTableAsync.createNewPlayer(s);

        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return createPlayerAccount(offlinePlayer.getName());

    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return createPlayerAccount(s);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer.getName());
    }
}
