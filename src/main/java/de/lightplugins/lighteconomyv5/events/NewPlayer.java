package de.lightplugins.lighteconomyv5.events;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
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
                Bukkit.getLogger().log(Level.INFO, "User already existing in Database.... ");
            } else {
                moneyTableAsync.createNewPlayer(player.getName()).thenAccept(success -> {
                    if(success) {
                        Bukkit.getLogger().log(Level.INFO, "New Player was putting in database!");
                    }
                });
            }
        });
    }
}
