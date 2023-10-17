package de.lightplugins.economy.database.querys;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BankTableAsync {

    public Main plugin;
    private final String tableName = "BankTable";

    public BankTableAsync(Main plugin) {
        this.plugin = plugin;
    }


    public CompletableFuture<List<String>> getTrustedMembers(Player player) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            List<String> allTrustedMembers = new ArrayList<>();

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("SELECT trusted FROM " + tableName + " WHERE uuid=?");
                ps.setString(1, player.getUniqueId().toString());

                ResultSet rs = ps.executeQuery();

                if(rs.next()) {

                    String[] result = rs.getString("trusted").split(";");

                    allTrustedMembers.addAll(Arrays.asList(result));
                    return allTrustedMembers;
                }

                return allTrustedMembers;

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



    public CompletableFuture<Double> playerBankBalance(String playerName) {

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

    public CompletableFuture<Integer> playerCurrentBankLevel(String playerName) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            try {

                connection = plugin.ds.getConnection();

                if(offlinePlayer != null) {
                    ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid=?");
                    ps.setString(1, offlinePlayer.getUniqueId().toString());
                } else {
                    ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE name=?");
                    ps.setString(1, playerName);
                }

                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    return rs.getInt("level");
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


    public CompletableFuture<Boolean> createBankAccount(String playerName) {


        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            Main.debugPrinting.sendInfo("New User found. Creating bank account for " + playerName);

            try {

                connection = plugin.ds.getConnection();
                ps = connection.prepareStatement("INSERT INTO " + tableName +" (uuid,name,money,level) VALUES (?,?,?,?)");

                if(offlinePlayer != null) {
                    ps.setString(1, offlinePlayer.getUniqueId().toString());
                } else {
                    UUID uuid = UUID.randomUUID();
                    ps.setString(1, uuid.toString());
                }
                ps.setString(2, playerName);
                ps.setDouble(3, 0.0);
                ps.setInt(4, 1);
                ps.execute();
                ps.close();
                Main.debugPrinting.sendInfo("Successfully created new Bankaccount for " + playerName);
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

    public CompletableFuture<Boolean> updatePlayerBankName(String playerName) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            if(offlinePlayer == null) {
                return false;
            }

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("UPDATE "+ tableName +" SET name=? WHERE uuid=?");
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

    public CompletableFuture<Boolean> setBankLevel(String playerName, int level) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            if(offlinePlayer == null) {
                return false;
            }

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("UPDATE "+ tableName +" SET level=? WHERE name=?");
                ps.setInt(1, level);
                ps.setString(2, offlinePlayer.getName());
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


    public CompletableFuture<Boolean> setBankMoney(String playerName, double amount) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            double fixedAmount = Main.util.fixDouble(amount);

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);

            try {

                connection = plugin.ds.getConnection();


                if(offlinePlayer != null) {
                    ps = connection.prepareStatement("UPDATE " + tableName + " SET money=? WHERE uuid=?");
                    ps.setString(2, offlinePlayer.getUniqueId().toString());
                } else {
                    ps = connection.prepareStatement("UPDATE " + tableName + " SET money=? WHERE name=?");
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
