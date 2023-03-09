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
    public void onjoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(plugin);
        moneyTableAsync.getPlayerData(player.getName()).thenAccept(resultSet -> {

            if(resultSet != null) {
                try {
                    String newUser = resultSet.getString("name");
                    Bukkit.getLogger().log(Level.INFO, "User already existing in Database.... " + newUser);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
