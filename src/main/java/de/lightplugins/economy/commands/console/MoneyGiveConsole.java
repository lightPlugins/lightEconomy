package de.lightplugins.economy.commands.console;

import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class MoneyGiveConsole extends SubCommand {
    @Override
    public String getName() {
        return "give";
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
            if(args[0].equalsIgnoreCase("give")) {
                String target = args[1];
                try {
                    double amount = Double.parseDouble(args[2]);
                    String currency = Main.economyImplementer.currencyNameSingular();

                    if(amount < 0) {
                        Bukkit.getConsoleSender().sendMessage(
                                Main.consolePrefix + "You can add only positiv numbers!");
                        return true;
                    }

                    if(!Main.economyImplementer.hasAccount(target)) {
                        Bukkit.getConsoleSender().sendMessage(
                                Main.consolePrefix + "The Target does not have an account or the name is wrong!");
                        return false;
                    }

                    EconomyResponse economyResponse = Main.economyImplementer.depositPlayer(target, amount);

                    if(economyResponse.transactionSuccess()) {
                        Bukkit.getConsoleSender().sendMessage(
                                Main.consolePrefix + "Successfully added " + target + " " + args[2] + " " + currency);
                        return true;

                    }
                    Bukkit.getConsoleSender().sendMessage(
                            Main.consolePrefix + "Something went wrong. Please try it again");

                } catch (NumberFormatException e) {
                    Bukkit.getConsoleSender().sendMessage(
                            Main.consolePrefix + "Please use a valid number and try it again.");
                    return false;
                }
            }
            Bukkit.getConsoleSender().sendMessage(
                    Main.consolePrefix + "Wrong command. Please use /eco add [playername] [amount]");
            return false;
        }

        Bukkit.getConsoleSender().sendMessage(
                Main.consolePrefix + "Wrong command. Please use /eco add [playername] [amount]");
        return false;
    }
}
