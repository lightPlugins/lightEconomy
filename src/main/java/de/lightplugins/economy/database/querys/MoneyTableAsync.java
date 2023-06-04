package de.lightplugins.economy.database.querys;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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

    public CompletableFuture<HashMap<String, Double>> getPlayersBalanceList() {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("SELECT * FROM "+ tableName + " WHERE isPlayer = '1'");

                HashMap<String, Double> playerList = new HashMap<>();

                ResultSet rs = ps.executeQuery();

                while(rs.next()) {

                    playerList.put(rs.getString("name"), rs.getDouble("money"));
                }
                return playerList;

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
            FileConfiguration settings = Main.settings.getConfig();
            double startBalance = settings.getDouble("settings.start-balance");

            Main.debugPrinting.sendInfo("New User found. Creating Database entry for " + playerName);

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
                    startBalance = 0.0;
                }
                ps.setString(2, playerName);
                ps.setDouble(3, startBalance);
                ps.execute();
                ps.close();
                Main.debugPrinting.sendInfo("Successfully added new Player to database!");
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

    public CompletableFuture<Boolean> updatePlayerName(String playerName) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            if(offlinePlayer == null) {
                return false;
            }

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("UPDATE MoneyTable SET name=? WHERE uuid=?");
                ps.setString(1, offlinePlayer.getName());
                ps.setString(2, offlinePlayer.getUniqueId().toString());
                ps.execute();
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

    public CompletableFuture<Boolean> deleteAccount(String playerName) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();
                connection.setAutoCommit(false);

                ps = connection.prepareStatement("DELETE FROM MoneyTable WHERE name=?");
                ps.setString(1, playerName);
                ps.execute();
                ps.close();
                connection.commit();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;

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
