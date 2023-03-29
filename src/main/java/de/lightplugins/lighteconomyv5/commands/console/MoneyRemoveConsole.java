package de.lightplugins.lighteconomyv5.commands.console;

import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class MoneyRemoveConsole extends SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "remove money throw console";
    }

    @Override
    public String getSyntax() {
        return "/eco remove [playername] [amount]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("remove")) {
                String target = args[1];
                try {
                    double amount = Double.parseDouble(args[2]);
                    String currency = Main.economyImplementer.currencyNameSingular();

                    if(!Main.economyImplementer.hasAccount(target)) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "The Target does not have an account or the name is wrong!");
                        return false;
                    }

                    if(!Main.economyImplementer.has(target, amount)) {
                        amount = Main.economyImplementer.getBalance(target);
                    }

                    EconomyResponse economyResponse = Main.economyImplementer.withdrawPlayer(target, amount);

                    if(economyResponse.transactionSuccess()) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "Successfully removed " + args[2] + " " + currency + " from " + target);
                        return true;

                    }
                    Bukkit.getLogger().log(Level.WARNING, "Something went wrong. Please try it again");

                } catch (NumberFormatException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Please use a valid number and try it again.");
                    return false;
                }
            }
        }

        return false;
    }
}
