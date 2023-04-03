package de.lightplugins.lighteconomyv5.master;

import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.lighteconomyv5.commands.*;
import de.lightplugins.lighteconomyv5.database.DatabaseConnection;
import de.lightplugins.lighteconomyv5.database.tables.CreateTable;
import de.lightplugins.lighteconomyv5.events.NewPlayer;
import de.lightplugins.lighteconomyv5.files.FileManager;
import de.lightplugins.lighteconomyv5.hooks.VaultHook;
import de.lightplugins.lighteconomyv5.implementer.EconomyImplementer;
import de.lightplugins.lighteconomyv5.utils.ColorTranslation;
import de.lightplugins.lighteconomyv5.utils.ProgressionBar;
import de.lightplugins.lighteconomyv5.utils.Util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Main getInstance;
    public static String currencyName;

    public static EconomyImplementer economyImplementer;
    private VaultHook vaultHook;
    public Economy econ;    // current null!!!

    public HikariDataSource ds;
    public DatabaseConnection hikari;

    public static ColorTranslation colorTranslation;
    public static ProgressionBar progressionBar;
    public static Util util;

    public static FileManager settings;
    public static FileManager messages;
    public static FileManager mainMenu;
    public static FileManager titles;

    public void onLoad() {

        /*  Initialize the Plugins instance  */

        getInstance = this;

        /*  Setup Economy Implemention & hook Vault  */

        economyImplementer = new EconomyImplementer();
        vaultHook = new VaultHook();
        vaultHook.hook();

        /*  Utility setup like FileManager & Color Translation  */

        colorTranslation = new ColorTranslation();
        util = new Util();

        settings = new FileManager(this, "settings.yml");
        messages = new FileManager(this, "messages.yml");
        mainMenu = new FileManager(this, "main-menu.yml");
        titles = new FileManager(this, "titles.yml");

        currencyName = settings.getConfig().getString("settings.currency-name");

        Bukkit.getLogger().log(Level.INFO, "Successfully loaded " + this.getName());
    }

    public void onEnable() {

        /*  Initalize Database and connect driver  */

        this.hikari = new DatabaseConnection(this);
        Bukkit.getLogger().log(Level.INFO, "Use MySQL Connection ...");

        if(settings.getConfig().getBoolean("mysql.enable")) {
            hikari.connectToDataBaseViaMariaDB();
        } else {
            hikari.connectToDatabaseViaSQLite();
        }



        /*  Creating needed Database-Tables  */

        Bukkit.getLogger().log(Level.INFO, "Creating Database ...");
        CreateTable createTable = new CreateTable(this);
        createTable.createMoneyTable();

        /*  Register required Commands & TabCompletion  */

        Bukkit.getLogger().log(Level.INFO, "Register Commands and TabCompletion ...");
        Objects.requireNonNull(this.getCommand("le")).setExecutor(new MainCommandManager(this));
        Objects.requireNonNull(this.getCommand("le")).setTabCompleter(new MainTabCompletion());

        Objects.requireNonNull(this.getCommand("money")).setExecutor(new MoneyCommandManager(this));
        Objects.requireNonNull(this.getCommand("money")).setTabCompleter(new MoneyTabCompletion());

        //  Console commands not require TabCompletion
        Objects.requireNonNull(this.getCommand("eco")).setExecutor(new ConsoleCommandManager(this));
        // Pay Commands not require TabCompletion
        Objects.requireNonNull(this.getCommand("pay")).setExecutor(new PayCommandMaster());


        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new NewPlayer(this), this);

        Bukkit.getLogger().log(Level.INFO, "Successfully started " + this.getName());
    }

    public void onDisable() {

        /*  Unhook Vaut Service Provider  */

        vaultHook.unhook();

        /*  Closing Database connection  */

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
