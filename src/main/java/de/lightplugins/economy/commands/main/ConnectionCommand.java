package de.lightplugins.economy.commands.main;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class ConnectionCommand extends SubCommand {
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
                Main.util.sendMessage(player, "&4Database has errors. &7Please check the log! ");
            }

            if(Main.economyImplementer.hasAccount(player.getName())) {
                Main.util.sendMessage(player, "&7Database is successfully connected to &c" + databaseSource);
                return false;
            }

            Main.util.sendMessage(
                    player, "&4Database connection test failed. &7Please check the log for more information");
            return false;
        }

        return false;
    }
}
