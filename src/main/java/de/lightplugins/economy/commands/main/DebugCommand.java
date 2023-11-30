package de.lightplugins.economy.commands.main;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DebugCommand extends SubCommand {
    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "Get an overview of the plugins state";
    }

    @Override
    public String getSyntax() {
        return "/le debug";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 1) {

            if (!player.hasPermission(PermissionPath.Debug.getPerm())) {
                Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                return false;
            }

            FileConfiguration settings = Main.settings.getConfig();

            String databaseSource = "sqlite";

            if(settings.getBoolean("mysql.enable")) {
                databaseSource = "mysql";
            }

            if(!Main.getInstance.ds.isRunning()) {
                List<String> messageList = new ArrayList<>();
                messageList.add("&7&m-----&r&c●&7&m-----&r &c&l&olight&7Economy &7&m-----&r&c●&7&m-----&r");
                messageList.add(" ");
                messageList.add("&8● &4Database has errors. &7Please check the log! SQL Mode: &c" + databaseSource);
                messageList.add("&8● &7Bukkit Version: &c" + Main.getInstance.getServer().getBukkitVersion());
                messageList.add("&8● &7Economy Version: &c" + Main.getInstance.getDescription().getVersion());
                messageList.add(" ");
                messageList.add("&7&m-----&r&c●&7&m-----&r &7&m-----&r&c●&7⏺&c●&7&m-----&r &7&m-----&r&c●&7&m-----&r");
                Main.util.sendMessageList(player, messageList);
                return false;
            }

            long startTime = System.currentTimeMillis();

            MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

            CompletableFuture<HashMap<String, Double>> test = moneyTableAsync.getPlayersBalanceList();

            int tableSize = test.get().size();

            long endTime = System.currentTimeMillis();
            long queryDuration = endTime - startTime;

            int totalConnections = Main.getInstance.ds.getHikariPoolMXBean().getTotalConnections();
            int activeConnections = Main.getInstance.ds.getHikariPoolMXBean().getActiveConnections();
            int pendingThreads = Main.getInstance.ds.getHikariPoolMXBean().getThreadsAwaitingConnection();
            int idleConnections = Main.getInstance.ds.getHikariPoolMXBean().getIdleConnections();

            if(Main.economyImplementer.hasAccount(player.getName())) {
                List<String> messageList = new ArrayList<>();
                messageList.add("&7&m-----&r&c●&7&m-----&r &c&l&olight&7Economy &7&m-----&r&c●&7&m-----&r");
                messageList.add(" ");
                messageList.add("&8● &7Database is &asuccessfully &7connected to &c" + databaseSource);
                messageList.add("  &8- &7Total Connections: &c" + totalConnections);
                messageList.add("  &8- &7Active Connections: &c" + activeConnections);
                messageList.add("  &8- &7Pending Connections: &c" + pendingThreads);
                messageList.add("  &8- &7Users in Database: &c" + tableSize + " &7(took &c" + queryDuration +"&7ms)");
                messageList.add("&8● &7Bukkit Version: &c" + Main.getInstance.getServer().getBukkitVersion());
                messageList.add("&8● &7Economy Version: &c" + Main.getInstance.getDescription().getVersion());
                messageList.add(" ");
                messageList.add("&7&m-----&r&c●&7&m-----&r &7&m-----&r&c●&7⏺&c●&7&m-----&r &7&m-----&r&c●&7&m-----&r");
                Main.util.sendMessageList(player, messageList);
                return false;
            }

            Main.util.sendMessage(
                    player, "&4Database connection test failed. &7Please check the log for more information");
            return false;
        }

        return false;
    }
}
