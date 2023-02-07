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

    public void create() {

        TableStatements tableStatements = new TableStatements(plugin);
        String statement =
                "CREATE TABLE IF NOT EXISTS BankTable (" +
                        "id INTEGER NOT NULL AUTO_INCREMENT, " +
                        "accountName VARCHAR(100), " +
                        "accountOwner VARCHAR(100), " +
                        "money DOUBLE, " +
                        "PRIMARY KEY (id))";

        tableStatements.createTableStatement(statement).thenAccept(resultSet -> {
            Bukkit.getLogger().log(Level.INFO, "Successfully created MoneyTable!");
        });
    }
}
