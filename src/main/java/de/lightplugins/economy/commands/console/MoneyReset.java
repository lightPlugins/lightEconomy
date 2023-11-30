package de.lightplugins.economy.commands.console;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class MoneyReset extends SubCommand {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "delete a player from the database";
    }

    @Override
    public String getSyntax() {
        return "/eco reset [PLAYERNAME]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length != 2)  {
            Bukkit.getLogger().log(Level.WARNING, "This command does not exist. Please try /eco delete PLAYERNAME");
            return false;
        }

        String target = args[1];

        if(!Main.economyImplementer.hasAccount(target)) {
            Bukkit.getLogger().log(Level.WARNING,
                    Main.consolePrefix + "The Target does not have an account and cant be deleted!");
            return false;
        }

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

        CompletableFuture<Boolean> delete = moneyTableAsync.deleteAccount(target);
            if(delete.get()) {
                Bukkit.getLogger().log(Level.INFO, Main.consolePrefix + "Successfully deleted user " + target);
                Player tar = Bukkit.getPlayer(target);
                if(tar == null) { return false; }
                tar.kickPlayer("§7[lightEconomy] §cYour economy account was deleted. " +
                        "\n§cPlease connect again to the server");
            } else {
                Bukkit.getLogger().log(Level.INFO, Main.consolePrefix + "Cannot delete user " + target + ". " +
                        "Something went wrong");
            }

        return false;
    }
}
