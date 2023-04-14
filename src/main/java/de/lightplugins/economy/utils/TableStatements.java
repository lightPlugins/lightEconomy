package de.lightplugins.economy.utils;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class TableStatements {
    public Main plugin;
    public TableStatements(Main plugin) {
        this.plugin = plugin;
    }

    public void createTableStatement(String statement) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = plugin.ds.getConnection();

            ps = connection.prepareStatement(statement);
            ps.executeUpdate();
            //connection.commit();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();

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
    }
}