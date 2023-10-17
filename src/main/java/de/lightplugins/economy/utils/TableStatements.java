package de.lightplugins.economy.utils;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;

import java.sql.*;
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
    /*
    *
    *   If lightEconomy becomes updates, here is the methode to check
    *   if the row is existing in current table. Else, we will add this
    *   row.
    *
     */
    public void checkTableUpdate(String rowName, String dataType, String tableName) {
        Connection connection = null;
        PreparedStatement psAdd = null;

        try {
            connection = plugin.ds.getConnection();

            DatabaseMetaData meta = connection.getMetaData();
            ResultSet result = meta.getColumns(null, null, tableName, rowName);

            if (!result.next()) {
                // Die Spalte existiert nicht, fügen Sie sie hinzu
                String statementString = "ALTER TABLE " + tableName + " ADD " + rowName + " " + dataType;
                try {
                    Bukkit.getLogger().log(Level.INFO, "[lightEconomy] FIRST TRY - Add row " + rowName + " into table " + tableName);
                    psAdd = connection.prepareStatement(statementString);
                    psAdd.executeUpdate();
                    Bukkit.getLogger().log(Level.INFO, "[lightEconomy] FIRST TRY - Successfully added row " + rowName + " into table " + tableName);
                } catch (SQLException ex) {
                    // Für den Fall, dass der erste Ansatz nicht funktioniert (z. B. bei SQLite), versuchen Sie eine alternative Syntax
                    Bukkit.getLogger().log(Level.WARNING, "[lightEconomy] SECOND TRY - Add row " + rowName + " into table " + tableName);
                    statementString = "ALTER TABLE " + tableName + " ADD COLUMN " + rowName + " " + dataType;
                    psAdd = connection.prepareStatement(statementString);
                    psAdd.executeUpdate();
                    Bukkit.getLogger().log(Level.WARNING, "[lightEconomy] SECOND TRY - Successfully added row " + rowName + " into table " + tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (psAdd != null) {
                try {
                    psAdd.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}