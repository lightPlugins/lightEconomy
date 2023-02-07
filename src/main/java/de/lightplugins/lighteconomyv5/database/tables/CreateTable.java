package de.lightplugins.lighteconomyv5.database.tables;

import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class CreateTable {

    public Main plugin;
    public CreateTable(Main plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Boolean> create() {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();
                ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS BankTable (" +
                        "id INTEGER NOT NULL AUTO_INCREMENT, " +
                        "accountName VARCHAR(100), " +
                        "accountOwner VARCHAR(100), " +
                        "accountTrusted VARCHAR(10000), " +
                        "money DOUBLE, " +
                        "level INTEGER, " +
                        "PRIMARY KEY (id))");
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
