package de.lightplugins.lighteconomyv5.commands.money;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTable;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

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

        try {

            double amount = Double.parseDouble(args[2]);

            if(amount == 0) {
                Main.util.sendMessage(player, MessagePath.NotZero.getPath()
                        .replace("#min-amount#", "0.01")
                        .replace("#currency#", Main.econ.currencyNameSingular()));
                return true;
            }

            if(amount < 0) {
                Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                return true;
            }

            MoneyTable moneyTable = new MoneyTable(Main.getInstance);



            moneyTable.getSinglePlayer(args[1]).thenAccept(result -> {

                try {

                    assert result != null;
                    try {
                        double currentBalance = result.getDouble("money");
                        double newBalance = currentBalance + amount;

                        moneyTable.addMoney(offlinePlayer.getName(), newBalance).thenAccept(success -> {

                            Main.util.sendMessage(player, MessagePath.MoneyAddPlayer.getPath()
                                    .replace("#amount#", String.valueOf(amount))
                                    .replace("#currency#", Main.econ.currencyNameSingular())
                                    .replace("#target#", args[1]));

                        });

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (AssertionError aError) {
                    Main.util.sendMessage(player, MessagePath.PlayerNotFound.getPath());
                }
            });

        } catch (NumberFormatException notANumber) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
        }
        return false;
    }
}
