package de.lightplugins.economy.commands.console;

import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class PluginReloadConsole extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin throw the console";
    }

    @Override
    public String getSyntax() {
        return "/eco reload";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {

                Main.messages.reloadConfig("messages.yml");
                Main.titles.reloadConfig("titles.yml");
                Main.voucher.reloadConfig("voucher.yml");

                Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Successfully reloaded the message.yml and titles.yml");
                Bukkit.getLogger().log(Level.WARNING, "[lightEconomy] If you changed the settings.yml please restart the server!");
            }
        }

        return false;
    }
}
