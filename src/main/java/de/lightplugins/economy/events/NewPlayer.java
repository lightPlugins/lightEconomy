package de.lightplugins.economy.events;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.database.querys.PlayerTable;
import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NewPlayer implements Listener {

    public Main plugin;
    public NewPlayer(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        /*

            TODO: Rename feature before create Player !!!

         */

        Player player = event.getPlayer();

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(plugin);
        BankTableAsync bankTableAsync = new BankTableAsync(plugin);
        moneyTableAsync.playerBalance(player.getName()).thenAccept(balance -> {

            FileConfiguration settings = Main.settings.getConfig();

            if(balance != null) {
                Main.debugPrinting.sendInfo("User already existing in Database. Checking for Name update ... ");

                moneyTableAsync.updatePlayerName(player.getName()).thenAccept(result -> {

                    if(result) {
                        Main.debugPrinting.sendInfo("Playername updated from PlayerTable via async");
                        return;
                    }

                    Main.debugPrinting.sendInfo("Playername failed from PlayerTable via async");

                });

            } else {
                moneyTableAsync.createNewPlayer(player.getName()).thenAccept(success -> {
                    if(success) {

                        Main.debugPrinting.sendInfo("New Player was putting in database!");



                        if(!settings.getBoolean("settings.enable-first-join-message")) {
                            return;
                        }

                        FileConfiguration messages = Main.messages.getConfig();

                        double startBalance = settings.getDouble("settings.start-balance");

                        Main.util.sendMessage(player, Objects.requireNonNull(messages.getString("firstJoinMessage"))
                                .replace("#startbalance#", String.valueOf(startBalance))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    }
                });
            }
        });

        CompletableFuture<Double> hasAccountValue = bankTableAsync.playerBankBalance(player.getName());

        try {
            if(hasAccountValue.get() == null) {

                CompletableFuture<Boolean> completableFuture = bankTableAsync.createBankAccount(player.getName());

                try {
                    if(completableFuture.get()) {
                        Main.debugPrinting.sendInfo(
                                "Successfully created bank account!");
                    } else {
                        Main.debugPrinting.sendInfo(
                                "Something went wrong in creating bank account for " + player.getName());
                    }
                    return;

                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            bankTableAsync.updatePlayerBankName(player.getName()).thenAcceptAsync(result -> {

                if(result) {
                    Main.debugPrinting.sendInfo("Playername updated from Banktable via async");
                    return;
                }

                Main.debugPrinting.sendInfo("Playername failed from Banktable via async");

            });

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
