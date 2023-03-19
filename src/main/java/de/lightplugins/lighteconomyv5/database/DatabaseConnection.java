package de.lightplugins.lighteconomyv5.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DatabaseConnection {

    public Main plugin;
    public DatabaseConnection(Main plugin) {
        this.plugin = plugin;
    }

    public void connectToDataBaseViaMariaDB() {


        FileConfiguration config = Main.settings.getConfig();


        String host = config.getString("mysql.host");
        String port = config.getString("mysql.port");
        String database = config.getString("mysql.database");
        String user = config.getString("mysql.user");
        String password = config.getString("mysql.password");
        Boolean ssl = false;
        int connectionPoolSize = 10;

        HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" +host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.addDataSourceProperty("serverName", host);
        hikariConfig.addDataSourceProperty("port", port);
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("user", user);
        hikariConfig.addDataSourceProperty("password", password);
        hikariConfig.addDataSourceProperty("useSSL", ssl);
        hikariConfig.setMaximumPoolSize(connectionPoolSize);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        plugin.ds = new HikariDataSource(hikariConfig);


    }

    public void connectToDatabaseViaSQLite() {

        String database = "lightEconomy.db";

        File dataFolder = new File(plugin.getDataFolder(), database);

        if (!dataFolder.exists()) {
            try {
                if(!dataFolder.createNewFile()) {
                    plugin.getLogger().log(Level.SEVERE, "Cannot create " + database);
                    return;
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Cannot create " + database);
                e.printStackTrace();
            }
        } else{
            Bukkit.getLogger().log(Level.WARNING, "SQLite file already exists -> skipping");
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("SQLite");
        config.setJdbcUrl("jdbc:sqlite:" + dataFolder.getAbsolutePath());
        config.setMaximumPoolSize(10);
        plugin.ds = new HikariDataSource(config);


        Bukkit.getLogger().log(Level.WARNING, dataFolder.toString());

        Bukkit.getLogger().log(Level.INFO, "Successfully connected to SQLite !");

    }
}
