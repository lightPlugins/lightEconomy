package de.lightplugins.lighteconomyv5.database.querys;

import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MoneyTable {

    public Main plugin;
    public MoneyTable(Main plugin) {
        this.plugin = plugin;
    }

    private final String tablename = "MoneyTable";

    public CompletableFuture<ResultSet> getSinglePlayer(String playername) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playername);

            try {

                connection = plugin.ds.getConnection();

                if(offlinePlayer != null) {
                    ps = connection.prepareStatement("SELECT * FROM "+ tablename +" WHERE uuid=?");
                    ps.setString(1, offlinePlayer.getUniqueId().toString());
                } else {
                    ps = connection.prepareStatement("SELECT * FROM "+ tablename +" WHERE name=?");
                    ps.setString(1, playername);
                }

                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    ps.close();
                    return rs;
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

    public CompletableFuture<Boolean> createNewPlayer(String playername) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playername);

            Bukkit.getLogger().log(Level.INFO, "New User found. Creating Database entry for " + playername);

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
                ps.setString(2, playername);
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

    public CompletableFuture<Boolean> addMoney(String playername, double amount) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playername);

            try {

                connection = plugin.ds.getConnection();


                if(offlinePlayer != null) {
                    ps = connection.prepareStatement("UPDATE MoneyTable SET money=? WHERE uuid=?");
                    ps.setString(2, offlinePlayer.getUniqueId().toString());
                } else {
                    ps = connection.prepareStatement("UPDATE MoneyTable SET money=? WHERE name=?");
                    ps.setString(2, playername);
                }
                ps.setDouble(1, amount);
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
