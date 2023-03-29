package de.lightplugins.lighteconomyv5.commands.console;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class MoneySetConsole extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "set money throw console";
    }

    @Override
    public String getSyntax() {
        return "/eco set [playername] [amount]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("set")) {
                String target = args[1];
                String currency = Main.economyImplementer.currencyNameSingular();
                try {
                    double amount = Double.parseDouble(args[2]);

                    if(!Main.economyImplementer.hasAccount(target)) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "The Target does not have an account or the name is wrong! + " + args[1]);
                        return false;
                    }

                    if(amount > 999999999999.99) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "The Amount exceeds the Limit of 999,999,999,999.99");
                        return false;
                    }

                    MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
                    CompletableFuture<Boolean> completableFuture = moneyTableAsync.setMoney(target, amount);

                    if(completableFuture.get()) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "Successfully set " + args[2] + " " + currency + " to " + target);
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
