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
                        "id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                        "uuid TEXT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "money DOUBLE NOT NULL, " +
                        "isPlayer BOOL NOT NULL" +
                        ")";


        tableStatements.createTableStatement(statement);
    }
}
