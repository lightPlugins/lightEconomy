package de.lightplugins.economy.database.querys;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/*
 * ----------------------------------------------------------------------------
 *  This software and its source code, including text, graphics, and images,
 *  are the sole property of lightPlugins ("Author").
 *
 *  You are granted a non-exclusive, non-transferable, revocable license
 *  to use, copy, modify, and distribute this software, provided that you
 *  include this copyright notice in all copies.
 *
 *  Unauthorized reproduction or distribution of this software, or any portion
 *  of it, may result in severe civil and criminal penalties, and will be
 *  prosecuted to the maximum extent possible under the law.
 * ----------------------------------------------------------------------------
 */

/**
 * The {@code MoneyTableAsync} class provides asynchronous database operations
 * related to player money balances.
 * This software is developed and maintained by lightPlugins.
 * For inquiries, please contact @discord: .light4coding.
 *
 * @version 5.0
 * @since 2021-07-19
 */

public class MoneyTableAsync {

    public Main plugin;
    private final String tableName = "MoneyTable";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MoneyTableAsync(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Retrieves the balance of a player by their name.
     *
     * @param playerName The name of the player.
     * @return A CompletableFuture containing the player's balance, or null if not found.
     */
    public CompletableFuture<Double> playerBalance(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = preparePlayerBalanceQuery(playerName, connection)) {

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getDouble("money");
                }

                return null;
            } catch (SQLException e) {
                logError("An error occurred while retrieving player balance for " + playerName, e);
                return null;
            }
        });
    }

    /**
     * Retrieves a list of player balances.
     *
     * @return A CompletableFuture containing a map of player names and their balances, or null on error.
     */
    public CompletableFuture<HashMap<String, Double>> getPlayersBalanceList() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = preparePlayersBalanceListQuery(connection)) {

                ResultSet rs = ps.executeQuery();
                return extractPlayerBalances(rs);
            } catch (SQLException e) {
                logError("An error occurred while retrieving player balances.", e);
                return null;
            }
        });
    }

    /**
     * Creates a new player entry in the database.
     *
     * @param playerName The name of the new player.
     * @return A CompletableFuture indicating the success of the operation.
     */
    public CompletableFuture<Boolean> createNewPlayer(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = prepareNewPlayerInsert(playerName, connection)) {

                ps.execute();
                logInfo("Successfully added a new player to the database: " + playerName);
                return true;
            } catch (SQLException e) {
                logError("An error occurred while creating a new player entry for " + playerName, e);
                return null;
            }
        });
    }

    /**
     * Updates a player's name in the database.
     *
     * @param playerName The current name of the player.
     * @return A CompletableFuture indicating the success of the operation.
     */
    public CompletableFuture<Boolean> updatePlayerName(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);
            if (offlinePlayer == null) {
                return false;
            }

            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = preparePlayerNameUpdate(offlinePlayer, connection)) {

                ps.execute();
                logInfo("Successfully updated the name of player: " + playerName);
                return true;
            } catch (SQLException e) {
                logError("An error occurred while updating the name of player: " + playerName, e);
                return null;
            }
        });
    }

    /**
     * Sets the balance of a player in the database.
     *
     * @param playerName The name of the player.
     * @param amount     The new balance for the player.
     * @return A CompletableFuture indicating the success of the operation.
     */
    public CompletableFuture<Boolean> setMoney(String playerName, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            double fixedAmount = Main.util.fixDouble(amount);

            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = preparePlayerBalanceUpdate(playerName, fixedAmount, connection)) {

                ps.execute();
                Main.debugPrinting.sendInfo("Successfully set the balance of player: " + playerName + " to " + fixedAmount);
                return true;
            } catch (SQLException e) {
                logError("An error occurred while setting the balance of player: " + playerName, e);
                return null;
            }
        });
    }

    /**
     * Deletes a player's account from the database.
     *
     * @param playerName The name of the player.
     * @return A CompletableFuture indicating the success of the operation.
     */
    public CompletableFuture<Boolean> deleteAccount(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = prepareDeleteAccount(playerName, connection)) {

                ps.executeUpdate();
                logInfo("Successfully deleted the account of player: " + playerName);
                return true;
            } catch (SQLException e) {
                logError("An error occurred while deleting the account of player: " + playerName, e);
                return false;
            }
        });
    }

    private PreparedStatement preparePlayerBalanceQuery(String playerName, Connection connection) throws SQLException {
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);
        if (offlinePlayer != null) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid = ?");
            ps.setString(1, offlinePlayer.getUniqueId().toString());
            return ps;
        } else {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE name = ?");
            ps.setString(1, playerName);
            return ps;
        }
    }

    private PreparedStatement preparePlayersBalanceListQuery(Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT * FROM " + tableName + " WHERE isPlayer = '1'");
    }

    private HashMap<String, Double> extractPlayerBalances(ResultSet rs) throws SQLException {
        HashMap<String, Double> playerList = new HashMap<>();
        while (rs.next()) {
            playerList.put(rs.getString("name"), rs.getDouble("money"));
        }
        return playerList;
    }

    private PreparedStatement prepareNewPlayerInsert(String playerName, Connection connection) throws SQLException {
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);
        FileConfiguration settings = Main.settings.getConfig();
        double startBalance = settings.getDouble("settings.start-balance");

        PreparedStatement ps = connection.prepareStatement("INSERT INTO " + tableName + " (uuid, name, money, isPlayer) VALUES (?, ?, ?, ?)");

        if (offlinePlayer != null) {
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
        return ps;
    }

    private PreparedStatement preparePlayerNameUpdate(OfflinePlayer offlinePlayer, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE " + tableName + " SET name = ? WHERE uuid = ?");
        ps.setString(1, offlinePlayer.getName());
        ps.setString(2, offlinePlayer.getUniqueId().toString());
        return ps;
    }

    private PreparedStatement preparePlayerBalanceUpdate(String playerName, double amount, Connection connection) throws SQLException {
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerName);
        PreparedStatement ps;

        if (offlinePlayer != null) {
            ps = connection.prepareStatement("UPDATE " + tableName + " SET money = ? WHERE uuid = ?");
            ps.setString(2, offlinePlayer.getUniqueId().toString());
        } else {
            ps = connection.prepareStatement("UPDATE " + tableName + " SET money = ? WHERE name = ?");
            ps.setString(2, playerName);
        }

        ps.setDouble(1, amount);
        return ps;
    }

    private PreparedStatement prepareDeleteAccount(String playerName, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableName + " WHERE name = ?");
        ps.setString(1, playerName);
        return ps;
    }

    private void logError(String message, Throwable e) {
        logger.error(message, e);
    }

    private void logInfo(String message) {
        logger.info(message);
    }
}
