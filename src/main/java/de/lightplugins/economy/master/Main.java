package de.lightplugins.economy.master;

import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.economy.commands.*;
import de.lightplugins.economy.commands.tabcompletion.BankTabCompletion;
import de.lightplugins.economy.commands.tabcompletion.MainTabCompletion;
import de.lightplugins.economy.commands.tabcompletion.MoneyTabCompletion;
import de.lightplugins.economy.database.DatabaseConnection;
import de.lightplugins.economy.database.tables.CreateTable;
import de.lightplugins.economy.events.ClaimVoucher;
import de.lightplugins.economy.events.NewPlayer;
import de.lightplugins.economy.files.FileManager;
import de.lightplugins.economy.hooks.VaultHook;
import de.lightplugins.economy.implementer.EconomyImplementer;
import de.lightplugins.economy.listener.BankListener;
import de.lightplugins.economy.listener.LoseMoney;
import de.lightplugins.economy.placeholder.PlaceholderAPI;
import de.lightplugins.economy.utils.*;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    public static DebugPrinting debugPrinting;

    public static FileManager settings;
    public static FileManager messages;
    public static FileManager titles;
    public static FileManager voucher;
    public static FileManager bankMenu;
    public static FileManager bankLevelMenu;
    public static FileManager bankManager;
    public static FileManager lose;


    public static List<String> payToggle = new ArrayList<>();
    public List<Player> bankDepositValue = new ArrayList<>();
    public List<Player> bankWithdrawValue = new ArrayList<>();

    public static InventoryManager bankMenuInventoryManager;

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
        debugPrinting = new DebugPrinting();

        settings = new FileManager(this, "settings.yml");
        messages = new FileManager(this, "messages.yml");
        titles = new FileManager(this, "titles.yml");
        voucher = new FileManager(this, "voucher.yml");
        bankMenu = new FileManager(this, "bank-menu.yml");
        bankLevelMenu = new FileManager(this, "bank-level.yml");
        lose = new FileManager(this, "lose.yml");
        bankManager = new FileManager(this, "bank-manage.yml");

        currencyName = settings.getConfig().getString("settings.currency-name");

        Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Successfully loaded " + this.getName());

    }

    public void onEnable() {

        /*  creating bStats method  */

        enableBStats();
        debugPrinting.sendInfo("bStats successfully registered.");

        /*  Initalize Database and connect driver  */

        this.hikari = new DatabaseConnection(this);

        if(settings.getConfig().getBoolean("mysql.enable")) {
            hikari.connectToDataBaseViaMariaDB();
        } else {
            hikari.connectToDatabaseViaSQLite();
        }

        /*  Check if PlaceholderAPI installed  */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register(); // initial lightEconomy placeholder
            Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Hooked into PlaceholderAPI");

        }

        /*  Creating needed Database-Tables  */

        Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Creating Database ...");
        CreateTable createTable = new CreateTable(this);
        createTable.createMoneyTable();
        createTable.createBankTable();
        createTable.createPlayerData();

        /*  Check for lightEconomy database updates  */

        TableStatements tableStatements = new TableStatements(this);

        /*
         *
         *   17.10.2023 - UPDATE: bank share system
         *
         */

        tableStatements.checkTableUpdate("trusted", "TEXT", "BankTable");

        /*  Register required Commands & TabCompletion  */

        Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Register Commands and TabCompletions ...");
        Objects.requireNonNull(this.getCommand("le")).setExecutor(new MainCommandManager(this));
        Objects.requireNonNull(this.getCommand("le")).setTabCompleter(new MainTabCompletion());

        Objects.requireNonNull(this.getCommand("money")).setExecutor(new MoneyCommandManager(this));
        Objects.requireNonNull(this.getCommand("money")).setTabCompleter(new MoneyTabCompletion());

        Objects.requireNonNull(this.getCommand("bank")).setExecutor(new BankCommandManager(this));
        Objects.requireNonNull(this.getCommand("bank")).setTabCompleter(new BankTabCompletion());

        // Console commands not require TabCompletion
        Objects.requireNonNull(this.getCommand("eco")).setExecutor(new ConsoleCommandManager(this));
        // Pay Commands not require TabCompletion
        Objects.requireNonNull(this.getCommand("pay")).setExecutor(new PayCommandMaster());

        Objects.requireNonNull(this.getCommand("bank")).setExecutor(new BankCommandManager(this));


        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new NewPlayer(this), this);
        pluginManager.registerEvents(new ClaimVoucher(), this);
        pluginManager.registerEvents(new BankListener(this), this);
        pluginManager.registerEvents(new LoseMoney(), this);

        bankMenuInventoryManager = new InventoryManager(this);
        bankMenuInventoryManager.init();

        Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Successfully started " + this.getName());
    }

    public void onDisable() {

        /*  Unhook Vaut Service Provider  */

        vaultHook.unhook();

        /*  Closing Database connection  */

        try {
            if(ds != null) {
                Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Status of Database: " + ds.getConnection());
                Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Lets try to shutdown the database");
                Bukkit.getLogger().log(Level.WARNING, "[lightEconomy] Never 'relaod' the server!");
                ds.close();
                Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Successfully disconnected Database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Successfully stopped " + this.getName());
    }

    private void enableBStats() {
        int pluginId = 18401;
        Metrics metrics = new Metrics(this, pluginId);
    }
}
