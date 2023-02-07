package de.lightplugins.lighteconomyv5.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DatabaseConnection {

    public Main plugin;
    public DatabaseConnection(Main plugin) {
        this.plugin = plugin;
    }

    public void connectToDataBaseViaMariaDB() {


        String host = "db.beamplex.de";
        String port = "3306";
        String database = "s5_lighteconomy";
        String user = "u5_33mL9lqTH3";
        String password = "Tu709Tawbt3=ihGR.TlUTF3g";
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

        String database = "database.db";

        File dataFolder = new File(plugin.getDataFolder(), database);

        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Cannot create " + database);
                e.printStackTrace();
            }
        } else{
            Bukkit.getLogger().log(Level.WARNING, "SQLite file already exists -> skipping");
        }

        plugin.ds = new HikariDataSource();
        plugin.ds.setPoolName("SQLite");
        plugin.ds.setJdbcUrl("jdbc:sqlite:" + dataFolder);

        Bukkit.getLogger().log(Level.INFO, "Successfully connected to SQLite !");

    }
}
