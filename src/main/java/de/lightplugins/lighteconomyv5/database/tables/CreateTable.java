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
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "uuid TEXT, " +
                        "name TEXT, " +
                        "money DOUBLE, " +
                        "isPlayer BOOL" +
                        ")";


        tableStatements.createTableStatement(statement);
    }
}
