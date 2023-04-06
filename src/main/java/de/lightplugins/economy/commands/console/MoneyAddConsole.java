package de.lightplugins.economy.commands.console;

import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class MoneyAddConsole extends SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "add money throw console";
    }

    @Override
    public String getSyntax() {
        return "/eco add [playername] [amount]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("add")) {
                String target = args[1];
                try {
                    double amount = Double.parseDouble(args[2]);
                    String currency = Main.economyImplementer.currencyNameSingular();

                    if(amount < 0) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "You can add only positiv numbers!");
                        return true;
                    }

                    if(!Main.economyImplementer.hasAccount(target)) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "The Target does not have an account or the name is wrong!");
                        return false;
                    }

                    EconomyResponse economyResponse = Main.economyImplementer.depositPlayer(target, amount);

                    if(economyResponse.transactionSuccess()) {
                        Bukkit.getLogger().log(Level.INFO,
                                "Successfully added " + target + " " + args[2] + " " + currency);
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
