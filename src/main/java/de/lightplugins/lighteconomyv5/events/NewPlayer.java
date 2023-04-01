package de.lightplugins.lighteconomyv5.events;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

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
        moneyTableAsync.playerBalance(player.getName()).thenAccept(balance -> {

            if(balance != null) {
                Bukkit.getLogger().log(Level.INFO, "User already existing in Database. Checking for Name update ... ");

                moneyTableAsync.updatePlayerName(player.getName()).thenAccept(result -> {
                    // silence Playername update every time, if the player connect to the server
                });

            } else {
                moneyTableAsync.createNewPlayer(player.getName()).thenAccept(success -> {
                    if(success) {

                        Bukkit.getLogger().log(Level.INFO, "New Player was putting in database!");

                        FileConfiguration settings = Main.settings.getConfig();

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
    }
}
