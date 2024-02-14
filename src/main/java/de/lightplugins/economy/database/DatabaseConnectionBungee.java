package de.lightplugins.economy.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.economy.bungeecord.Bungee;
import net.md_5.bungee.config.Configuration;

public class DatabaseConnectionBungee {

    public Bungee plugin;
    public DatabaseConnectionBungee(Bungee plugin) {
        this.plugin = plugin;
    }

    public void connectToDataBaseViaMariaDB() {

        Configuration config = Bungee.database.getConfig();

        String host = config.getString("mysql.host");
        String port = config.getString("mysql.port");
        String database = config.getString("mysql.database");
        String user = config.getString("mysql.user");
        String password = config.getString("mysql.password");
        Boolean ssl = config.getBoolean("mysql.ssl");
        Boolean useServerPrepStmts = config.getBoolean("mysql.advanced.useServerPrepStmts");
        Boolean cachePrepStmts = config.getBoolean("mysql.advanced.cachePrepStmts");
        int prepStmtCacheSize = config.getInt("mysql.advanced.prepStmtCacheSize");
        int prepStmtCacheSqlLimit = config.getInt("mysql.advanced.prepStmtCacheSqlLimit");
        int connectionPoolSize = config.getInt("mysql.advanced.connectionPoolSize");

        System.out.println(Bungee.consolePrefix +
                "Connecting to jdbc:mariadb://§c" + host + "§r:§c" + port + "§r/§c" + database);

        HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("useSSL", ssl);
        hikariConfig.setMaximumPoolSize(connectionPoolSize);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", useServerPrepStmts);
        hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);

        plugin.ds = new HikariDataSource(hikariConfig);

    }
}
