package de.lightplugins.lighteconomyv5.commands.money;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTable;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.logging.Level;

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

        try {
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

                MoneyTable moneyTable = new MoneyTable(plugin);

                moneyTable.getSinglePlayer(args[1]).thenAccept(result -> {

                    try {
                        double currentBalance = result.getDouble("money");
                        double newBalance = currentBalance - amount;

                        if(newBalance < 0) {
                            newBalance = 0;
                        }

                        moneyTable.setMoney(args[1], newBalance).thenAccept(success -> {
                            Main.util.sendMessage(player, MessagePath.MoneyRemovePlayer.getPath()
                                    .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                                    .replace("#target#", args[1])
                                    .replace("#amount#", Main.util.formatDouble(amount))
                            );
                        });

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

            } catch (NumberFormatException e) {
                Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
                return true;
            }
        } catch (NumberFormatException e) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
        }


        return false;
    }
}
