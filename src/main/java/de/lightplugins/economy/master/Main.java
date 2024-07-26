package de.lightplugins.economy.master;

import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.economy.api.LightEconomyAPI;
import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.PluginMessagePath;
import de.lightplugins.economy.utils.PluginMessageListener;
import de.lightplugins.economy.commands.*;
import de.lightplugins.economy.commands.tabcompletion.BalanceTabCompletion;
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
import de.lightplugins.economy.listener.TimeReward;
import de.lightplugins.economy.placeholder.PapiRegister;
import de.lightplugins.economy.utils.*;
//import de.lightplugins.light.Light;
//import de.lightplugins.light.api.LightAPI;
//import de.lightplugins.light.api.creators.FutureCreator;
//import de.lightplugins.light.api.creators.PreparedCreator;
//import de.lightplugins.light.api.creators.StatementCreator;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main extends JavaPlugin {

    public static Main getInstance;
    public static String currencyName;

    public static final String consolePrefix = "§r[light§cEconomy§r] ";

    public static EconomyImplementer economyImplementer;
    private VaultHook vaultHook;
    public static boolean isCitizens = false;
    public Economy econ;    // current null!!!
    public boolean isBungee; //  create config in settings.yml
//    public static LightAPI lightAPI;

    public HikariDataSource ds;
    public DatabaseConnection hikari;
    public MoneyTableAsync moneyTable;
    public BankTableAsync bankTable;

    public static ColorTranslation colorTranslation;
    public static ProgressionBar progressionBar;
    public static Util util;
    public static Sounds sounds;
    public static DebugPrinting debugPrinting;

    public static LightEconomyAPI lightEconomy;

    public static FileManager settings;
    public static FileManager messages;
    public static FileManager titles;
    public static FileManager voucher;
    public static FileManager bankMenu;
    public static FileManager bankLevelMenu;
    public static FileManager lose;
    public static FileManager discord;
    public SignPackets signGui;


    public static List<String> payToggle = new ArrayList<>();
    public List<Player> bankDepositValue = new ArrayList<>();
    public List<Player> bankWithdrawValue = new ArrayList<>();

    public static InventoryManager bankMenuInventoryManager;

    public void onLoad() {

        /*  Initialize the Plugins instance  */

        getInstance = this;

//        lightAPI = new LightAPI(Light.getInstance);

        //testDataBase();

        /*  Setup Economy Implemention & hook Vault  */

        economyImplementer = new EconomyImplementer();
        lightEconomy = new LightEconomyAPI();

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
        //discord = new FileManager(this, "discordWebhook/discord.yml");

        currencyName = settings.getConfig().getString("settings.currency-name");

        sounds = new Sounds();
        Bukkit.getConsoleSender().sendMessage(consolePrefix + "Successfully loaded " + this.getName());

    }

    public void onEnable() {

        /*  creating bStats method  */

        Bukkit.getConsoleSender().sendMessage("\n " +
                " §r_      _____ _____ _    _ _______ §c______ _____ ____  _   _  ____  __  ____     __\n" +
                " §r| |    |_   _/ ____| |  | |__   __§c|  ____/ ____/ __ \\| \\ | |/ __ \\|  \\/  \\ \\   / /\n" +
                " §r| |      | || |  __| |__| |  | |  §c| |__ | |   | |  | |  \\| | |  | | \\  / |\\ \\_/ / \n" +
                " §r| |      | || | |_ |  __  |  | |  §c|  __|| |   | |  | | . ` | |  | | |\\/| | \\   /  \n" +
                " §r| |____ _| || |__| | |  | |  | |  §c| |___| |___| |__| | |\\  | |__| | |  | |  | |   \n" +
                " §r|______|_____\\_____|_|  |_|  |_|  §c|______\\_____\\____/|_| \\_|\\____/|_|  |_|  |_|" +
                "\n\n" + ChatColor.RESET +
                "      Version: §c5.5.2   §rAuthor: §clightPlugins\n" +
                "      §rThank you for using lightEconomy. If you came in trouble feel free to join\n" +
                "      my §cDiscord §rserver: https://discord.gg/G2EuzmSW\n");

        enableBStats();
        debugPrinting.sendInfo("bStats successfully registered.");

        /*  Check if PlaceholderAPI is installed  */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getConsoleSender().sendMessage("\n\n    §4ERROR\n\n" +
                    "    §cCould not find §4PlaceholderAPI \n" +
                    "    §rLighteconomy will §cnot run §rwithout PlaceholderAPI. Please download\n" +
                    "    the latest version of PAPI \n" +
                    "    §chttps://www.spigotmc.org/resources/placeholderapi.6245/ \n\n\n");
            onDisable();
            return;
        } else {
            Bukkit.getConsoleSender().sendMessage(consolePrefix + "Successfully hooked into §cPlaceholderAPI");
            new PapiRegister().register(); // initial lightEconomy placeholder
            Bukkit.getConsoleSender().sendMessage(consolePrefix + "Successfully registered lightEconomy placeholders");
        }

        /*  Check if ProtocoLib is installed  */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            if(settings.getConfig().getBoolean("settings.bankInputViaSign.enable")) {
                Bukkit.getConsoleSender().sendMessage("\n\n    §4ERROR\n\n" +
                        "    §cCould not find §4ProtocolLib\n" +
                        "    §rYou enabled bankInputViaSign while ProtocolLib is not installed on this Server.\n" +
                        "    §rDownload the latest version or change §cbankInputViaSign §7to §cfalse!\n" +
                        "    §chttps://ci.dmulloy2.net/job/ProtocolLib/\n\n\n");
                onDisable();
                return;
            }
        } else {
            this.signGui = new SignPackets(this);
            Bukkit.getConsoleSender().sendMessage(consolePrefix + "Successfully hooked into §cProtocolLib");
        }

        /*  Initalize Database and connect driver  */

        this.hikari = new DatabaseConnection(this);

        if(settings.getConfig().getBoolean("mysql.enable")) {
            hikari.connectToDataBaseViaMariaDB();
        } else {
            hikari.connectToDatabaseViaSQLite();
        }

        this.moneyTable = new MoneyTableAsync(this);
        this.bankTable = new BankTableAsync(this);

        /*  Creating needed Database-Tables  */

        Bukkit.getConsoleSender().sendMessage(consolePrefix + "Creating Database ...");
        CreateTable createTable = new CreateTable(this);
        createTable.createMoneyTable();
        createTable.createBankTable();
        //createTable.createPlayerData();

        /*  Check for lightEconomy database updates  */

        TableStatements tableStatements = new TableStatements(this);

        /*  Register required Commands & TabCompletion  */

        Bukkit.getConsoleSender().sendMessage(consolePrefix + "Register Commands and TabCompletions ...");
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

        Objects.requireNonNull(this.getCommand("balance")).setExecutor(new BalanceCommandManager(this));
        Objects.requireNonNull(this.getCommand("balance")).setTabCompleter(new BalanceTabCompletion());


        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new NewPlayer(this), this);
        pluginManager.registerEvents(new ClaimVoucher(), this);
        pluginManager.registerEvents(new BankListener(this), this);
        pluginManager.registerEvents(new LoseMoney(), this);
        pluginManager.registerEvents(new TestEvent(), this);

        isBungee = settings.getConfig().getBoolean("settings.bungeecord");

        if(isBungee) {
            Bukkit.getConsoleSender().sendMessage(consolePrefix + "Enable Bungeecord features ...");
            this.getServer().getMessenger().registerOutgoingPluginChannel(
                    this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, PluginMessagePath.PAY.getType(), new PluginMessageListener());
        }

        bankMenuInventoryManager = new InventoryManager(this);
        bankMenuInventoryManager.init();

        // Starting the timer for the time reward feature
        TimeReward timeReward = new TimeReward();
        timeReward.startTimedReward();

        Bukkit.getConsoleSender().sendMessage(consolePrefix + "Successfully started " + this.getName());
    }


    public void onDisable() {

        /*  Unhook Vaut Service Provider  */

        vaultHook.unhook();

        /*  Closing Database connection  */

        try {
            if(ds != null && !ds.isClosed()) {
                Bukkit.getConsoleSender().sendMessage(consolePrefix + "Status of Database: " + ds.getConnection());
                Bukkit.getConsoleSender().sendMessage(consolePrefix + "Lets try to shutdown the database");
                Bukkit.getConsoleSender().sendMessage( consolePrefix + "§cHint: §4Never 'relaod' the server!");
                ds.close();
                Bukkit.getConsoleSender().sendMessage(consolePrefix + "Successfully disconnected Database!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong on closing database!", e);
        }

        Bukkit.getConsoleSender().sendMessage(consolePrefix + "Successfully stopped " + this.getName());
    }

    private void enableBStats() {
        int pluginId = 18401;
        Metrics metrics = new Metrics(this, pluginId);
    }


//    public void testDataBase() {
//        String query = lightAPI.getStatementCreator().createTableStatement(
//                "myTable", "id", "player TEXT", "balance REAL");
//
//
//        try (PreparedStatement ps = new PreparedCreator().preparedStatement(query)) {
//            // Führen Sie hier Ihre Operationen mit dem PreparedStatement aus
//            FutureCreator futureCreator = new FutureCreator();
//            CompletableFuture<Integer> test = futureCreator.executeUpdate(ps);
//
//            test.thenApplyAsync(result -> {
//                lightAPI.getDebugPrinting().print("database result: " + result);
//                return result;
//            }).thenAcceptAsync(result -> {
//                lightAPI.getDebugPrinting().print("Async processing completed with result: " + result);
//            }).join();
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Something went wrong on creating table!", e);
//        }
//    }
}
