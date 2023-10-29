package de.lightplugins.economy.database.querys;

import de.lightplugins.economy.master.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *
 * This software is developed and maintained by lightPlugins.
 * For inquiries, please contact @discord: .light4coding.
 *
 * @version 5.0
 * @since 2021-07-19
 */

public class PlayerTable {

    public Main plugin;
    public PlayerTable(Main plugin) {
        this.plugin = plugin;
    }
    private final String tableName = "PlayerData";
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Checks if a player is already trusted.
     *
     * @param uuid The UUID of the player to check.
     * @return True if trusted, false if not, or null if an error occurs.
     */
    public CompletableFuture<Boolean> alreadyTrusted(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid=?")) {

                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                } catch (SQLException e) {
                    // Fehler bei der Abfrage
                    logError("An error occurred while executing the SQL query.", e);
                    return null;
                }
            } catch (SQLException e) {
                // Fehler beim Aufbau der Verbindung oder Vorbereitung der Anweisung
                logError("An error occurred while setting up the database connection or preparing the statement.", e);
                return null;
            }
        });
    }

    /**
     * Retrieves a list of trusted banks for a player.
     *
     * @param uuid The UUID of the player.
     * @return A list of trusted bank names, or null if an error occurs.
     */
    public CompletableFuture<List<String>> getTrustedBanks(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT trustedBank FROM " + tableName + " WHERE uuid=?")) {

                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    List<String> uuidList = new ArrayList<>();

                    while (rs.next()) {
                        uuidList.add(rs.getString("trustedBank"));
                    }

                    return uuidList;
                } catch (SQLException e) {
                    logError("An error occurred while processing the SQL query result.", e);
                    return null;
                }
            } catch (SQLException e) {
                logError("An error occurred while setting up the database connection or preparing the statement.", e);
                return null;
            }
        });
    }

    /**
     * Retrieves a list of players that trust the given player.
     *
     * @param uuid The UUID of the player to check.
     * @return A list of UUIDs for players trusting the given player, or null if an error occurs.
     */
    public CompletableFuture<List<String>> getOwnTruster(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM " + tableName + " WHERE trustedBank=?")) {

                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    List<String> uuidList = new ArrayList<>();

                    while (rs.next()) {
                        uuidList.add(rs.getString("uuid"));
                    }

                    return uuidList;
                } catch (SQLException e) {
                    logError("An error occurred while processing the SQL query result.", e);
                    return null;
                }
            } catch (SQLException e) {
                logError("An error occurred while setting up the database connection or preparing the statement.", e);
                return null;
            }
        });
    }

    /**
     * Adds a trusted player to another player's list.
     *
     * @param uuid The UUID of the player who trusts.
     * @param targetBankAccountUserUUID The UUID of the player being trusted.
     * @return True if successful, false if an error occurs.
     */
    public CompletableFuture<Boolean> addTrustedPlayerTo(String uuid, String targetBankAccountUserUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT INTO " + tableName + " (uuid, trustedBank) VALUES (?, ?)")) {

                ps.setString(1, uuid);
                ps.setString(2, targetBankAccountUserUUID);
                ps.execute();
                return true;
            } catch (SQLException e) {
                logError("An error occurred while executing the SQL query.", e);
                return false; // In diesem Fall, wenn ein Fehler auftritt, wird false zurückgegeben.
            }
        });
    }

    /**
     * Removes a trusted player from a player's list.
     *
     * @param uuid The UUID of the player to remove from the trust list.
     * @return True if successful, false if an error occurs.
     */
    public CompletableFuture<Boolean> removeTrustedPlayer(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableName + " WHERE uuid = ?")) {

                ps.setString(1, uuid);
                int rowsAffected = ps.executeUpdate();

                // Überprüfe, ob Zeilen gelöscht wurden (d.h., ob das Entfernen erfolgreich war)
                return rowsAffected > 0;
            } catch (SQLException e) {
                logError("An error occurred while executing the SQL query.", e);
                return false; // In diesem Fall, wenn ein Fehler auftritt, wird false zurückgegeben.
            }
        });
    }

    private void logError(String message, Throwable e) {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.error(message, e);
    }
}
