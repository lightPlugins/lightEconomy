package de.lightplugins.lighteconomyv5.commands.money;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class MoneyRemoveCommand extends SubCommand {

    public Main plugin;
    public MoneyRemoveCommand(Main plugin) { this.plugin = plugin; }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Remove a specified amount of money";
    }

    @Override
    public String getSyntax() {
        return "/money remove [playerName] [amount]";
    }

    @Override
    public boolean perform(Player player, String[] args)  {


        if(args.length != 3) {  return true; }

        OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[1]);

        if(offlinePlayer == null) {
            Main.util.sendMessage(player, MessagePath.PlayerNotFound.getPath());
            return true;
        }

        try {
            double amount = Double.parseDouble(args[2]);

            if(amount == 0) {
                Main.util.sendMessage(player, MessagePath.NotZero.getPath());
                return true;
            }

            if(amount < 0) {
                Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                return true;
            }

            MoneyTableAsync moneyTableAsync = new MoneyTableAsync(plugin);

            moneyTableAsync.playerBalance(args[1]).thenAccept(balance -> {

                double currentBalance = balance;
                double newBalance = currentBalance - amount;

                if(newBalance < 0) {
                    newBalance = 0;
                }

                moneyTableAsync.setMoney(args[1], newBalance).thenAccept(success -> {
                    Main.util.sendMessage(player, MessagePath.MoneyRemovePlayer.getPath()
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                            .replace("#target#", args[1])
                            .replace("#amount#", Main.util.formatDouble(amount))
                    );
                });
            });

        } catch (NumberFormatException e) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
            return true;
        }


        return false;
    }
}
