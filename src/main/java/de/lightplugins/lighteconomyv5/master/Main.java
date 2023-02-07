package de.lightplugins.lighteconomyv5.master;

import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.lighteconomyv5.commands.MainCommandManager;
import de.lightplugins.lighteconomyv5.commands.MainTabCompletion;
import de.lightplugins.lighteconomyv5.database.DatabaseConnection;
import de.lightplugins.lighteconomyv5.database.tables.CreateTable;
import de.lightplugins.lighteconomyv5.hooks.VaultHook;
import de.lightplugins.lighteconomyv5.implementer.EconomyImplementer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Main getInstance;
    public EconomyImplementer economyImplementer;
    private VaultHook vaultHook;
    public Economy econ;
    public HikariDataSource ds;
    public DatabaseConnection hikari;

    public void onLoad() {

        /*  Initialize the Plugins instance  */

        getInstance = this;

        economyImplementer = new EconomyImplementer();
        vaultHook = new VaultHook();
        vaultHook.hook();
        Bukkit.getLogger().log(Level.INFO, "Successfully loaded " + this.getName());
    }

    public void onEnable() {

        this.hikari = new DatabaseConnection(this);
        Bukkit.getLogger().log(Level.INFO, "Use MySQL Connection ...");
        hikari.connectToDataBaseViaMariaDB();

        Bukkit.getLogger().log(Level.INFO, "Creating Database ...");
        CreateTable createTable = new CreateTable(this);
        createTable.create();

        Bukkit.getLogger().log(Level.INFO, "Register Commands and TabCompletion ...");
        Objects.requireNonNull(this.getCommand("le")).setExecutor(new MainCommandManager(this));
        Objects.requireNonNull(this.getCommand("le")).setTabCompleter(new MainTabCompletion());

        Bukkit.getLogger().log(Level.INFO, "Successfully started " + this.getName());
    }

    public void onDisable() {
        vaultHook.unhook();

        try {
            if(ds != null) {
                Bukkit.getLogger().log(Level.INFO, "Status of Database: " + ds.getConnection());
                Bukkit.getLogger().log(Level.INFO, "Lets try to shutdown the database");
                Bukkit.getLogger().log(Level.WARNING, "Never 'relaod' the server!");
                ds.close();
                Bukkit.getLogger().log(Level.INFO, "Successfully disconnected Database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getLogger().log(Level.INFO, "Successfully stopped " + this.getName());
    }
}
