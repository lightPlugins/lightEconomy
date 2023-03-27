package de.lightplugins.lighteconomyv5.database.tables;

import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.TableStatements;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class CreateTable {

    public Main plugin;
    public CreateTable(Main plugin) {
        this.plugin = plugin;
    }

    public void createMoneyTable() {


        TableStatements tableStatements = new TableStatements(plugin);
        String statement =
                "CREATE TABLE IF NOT EXISTS MoneyTable (" +
                        "uuid TEXT, " +
                        "name TEXT, " +
                        "money DOUBLE NOT NULL, " +
                        "isPlayer BOOL NOT NULL" +
                        ")";

        String tableName = "MoneyTable";
        String update = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "uuid TEXT,"
                + "name TEXT,"
                + "money DOUBLE,"
                + "isPlayer BOOL,"
                + "PRIMARY KEY (uuid))";


        tableStatements.createTableStatement(update);
    }
}
