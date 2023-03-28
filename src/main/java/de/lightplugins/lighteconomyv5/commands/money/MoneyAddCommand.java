package de.lightplugins.lighteconomyv5.commands.money;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class MoneyAddCommand extends SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "add Money to Player";
    }

    @Override
    public String getSyntax() {
        return "/money add [PlayerName] [Amount]";
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
                Main.util.sendMessage(player, MessagePath.NotZero.getPath()
                        .replace("#min-amount#", "0.01")
                        .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                return true;
            }

            if(amount < 0) {
                Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                return true;
            }


            MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

            moneyTableAsync.playerBalance(args[1]).thenAccept(balance -> {

                double currentBalance = balance;
                double newBalance = currentBalance + amount;

                moneyTableAsync.setMoney(offlinePlayer.getName(), newBalance).thenAccept(success -> {

                    Main.util.sendMessage(player, MessagePath.MoneyAddPlayer.getPath()
                            .replace("#amount#", Main.util.formatDouble(amount))
                            .replace("#target#", args[1])
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                            .replace("#balance#", Main.util.formatDouble(newBalance))
                    );

                });
            });

        } catch (NumberFormatException notANumber) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
        }
        return false;
    }
}
