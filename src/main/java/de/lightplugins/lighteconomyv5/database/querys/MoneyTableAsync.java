package de.lightplugins.lighteconomyv5.database.querys;

import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class MoneyTableAsync {

    public Main plugin;
    public MoneyTableAsync(Main plugin) {
        this.plugin = plugin;
    }

    private final String tableName = "MoneyTable";

    public CompletableFuture<Double> playerBalance(String playerName) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            try {

                connection = plugin.ds.getConnection();

                if(offlinePlayer != null) {
                    ps = connection.prepareStatement("SELECT * FROM "+ tableName +" WHERE uuid=?");
                    ps.setString(1, offlinePlayer.getUniqueId().toString());
                } else {
                    ps = connection.prepareStatement("SELECT * FROM "+ tableName +" WHERE name=?");
                    ps.setString(1, playerName);
                }

                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    return rs.getDouble("money");
                }

                return null;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public CompletableFuture<Boolean> createNewPlayer(String playerName) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            Bukkit.getLogger().log(Level.INFO, "New User found. Creating Database entry for " + playerName);

            try {

                connection = plugin.ds.getConnection();
                ps = connection.prepareStatement("INSERT INTO MoneyTable (uuid,name,money,isPlayer) VALUES (?,?,?,?)");

                if(offlinePlayer != null) {
                    ps.setString(1, offlinePlayer.getUniqueId().toString());
                    ps.setBoolean(4, true);
                } else {
                    UUID uuid = UUID.randomUUID();
                    ps.setString(1, uuid.toString());
                    ps.setBoolean(4, false);
                }
                ps.setString(2, playerName);
                ps.setDouble(3, 0.0);
                ps.execute();
                ps.close();
                Bukkit.getLogger().log(Level.INFO, "Successfully added new Player to database!");
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public CompletableFuture<Boolean> setMoney(String playerName, double amount) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            double fixedAmount = Main.util.fixDouble(amount);

            Bukkit.getLogger().log(Level.WARNING, "TEST - " + fixedAmount);

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            try {

                connection = plugin.ds.getConnection();


                if(offlinePlayer != null) {
                    ps = connection.prepareStatement("UPDATE MoneyTable SET money=? WHERE uuid=?");
                    ps.setString(2, offlinePlayer.getUniqueId().toString());
                } else {
                    ps = connection.prepareStatement("UPDATE MoneyTable SET money=? WHERE name=?");
                    ps.setString(2, playerName);
                }
                ps.setDouble(1, fixedAmount);
                ps.execute();
                ps.close();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
