package de.lightplugins.lighteconomyv5.utils;

import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class TableStatements {
    public Main plugin;
    public TableStatements(Main plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Boolean> createTableStatement(String statement) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();
                ps = connection.prepareStatement(statement);
                ps.execute();
                ps.close();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                        Bukkit.getLogger().log(Level.INFO, "Connection closed.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                        Bukkit.getLogger().log(Level.INFO, "Statement closed.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
