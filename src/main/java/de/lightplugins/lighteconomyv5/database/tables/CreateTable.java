package de.lightplugins.lighteconomyv5.database.tables;

import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.TableStatements;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

public class CreateTable {

    public Main plugin;
    public CreateTable(Main plugin) {
        this.plugin = plugin;
    }

    public void createMoneyTable() {

        String tableName = "MoneyTable";
        String tableStatement = "";
        TableStatements tableStatements = new TableStatements(plugin);
        FileConfiguration settings = Main.settings.getConfig();

        tableStatement = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                        + "uuid TEXT,"
                        + "name TEXT,"
                        + "money DOUBLE,"
                        + "isPlayer BOOL,"
                        + "PRIMARY KEY (uuid))";

        if(settings.getBoolean("mysql.enable")) {
            tableStatement = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + "uuid TEXT(200),"
                    + "name TEXT,"
                    + "money DOUBLE,"
                    + "isPlayer BOOL,"
                    + "PRIMARY KEY (uuid(200)))";
        }

        tableStatements.createTableStatement(tableStatement);
    }
}
