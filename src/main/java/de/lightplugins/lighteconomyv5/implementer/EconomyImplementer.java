package de.lightplugins.lighteconomyv5.implementer;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.master.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
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
        return Main.util.formatDouble(v);
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
            e.printStackTrace();
        }

        return false;
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
        CompletableFuture<Double> balance = moneyTableAsync.playerBalance(s);


        try {
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
    public boolean has(String s, double v) {
        return getBalance(s) >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return getBalance(offlinePlayer.getName()) >= v;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return getBalance(s) >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return getBalance(offlinePlayer.getName()) >= v;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {

        if(!hasAccount(s)) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The Player does not have an account");
        }

        if(!has(s, v)) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The Player has not enough money");
        }

        if(v < 0.0) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Cant withdraw negative numbers");
        }

        double currentBalance = getBalance(s);
        currentBalance -= v;

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        CompletableFuture<Boolean> completableFuture = moneyTableAsync.setMoney(s, currentBalance);

        try {
            if(completableFuture.get()) {

                FileConfiguration titles = Main.titles.getConfig();

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


                OfflinePlayer offlinePlayer = Bukkit.getPlayer(s);

                if(offlinePlayer != null && offlinePlayer.isOnline()) {

                    Player player = offlinePlayer.getPlayer();

                    if(titles.getBoolean("titles.withdraw-wallet.enable")) {
                        Main.util.countUp(player, v, upperTitle, lowerTitle, upperTitleFinal, lowerTitleFinal);
                    }
                }


                return new EconomyResponse(v, currentBalance, EconomyResponse.ResponseType.SUCCESS,
                        "[lightEconomy] Successfully withdraw");
            }

            return new EconomyResponse(v, currentBalance, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Something went wrong on withdraw");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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

        if(!hasAccount(s)) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The Player does not have an account");
        }

        if(v < 0.0) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] Cant deposit negative numbers");
        }

        if(v > 999999999999.99) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The deposit value is to big");
        }

        double currentBalance = getBalance(s);
        currentBalance += v;

        if(currentBalance > 999999999999.99) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "[lightEconomy] The player reached the max balance for his pocket");
        }

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        CompletableFuture<Boolean> completableFuture = moneyTableAsync.setMoney(s, currentBalance);

        try {
            if(completableFuture.get()) {

                FileConfiguration titles = Main.titles.getConfig();

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
                        Main.util.countUp(player, v, upperTitle, lowerTitle, upperTitleFinal, lowerTitleFinal);
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
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        CompletableFuture<Boolean> completableFuture = moneyTableAsync.createNewPlayer(offlinePlayer.getName());

        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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
