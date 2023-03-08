package de.lightplugins.lighteconomyv5.commands.money;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTable;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class MoneySetCommand extends SubCommand {

    Main plugin;
    public MoneySetCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "set a specified amount of Money to the targets Account";
    }

    @Override
    public String getSyntax() {
        return "/money set [playername]";
    }

    @Override
    public boolean perform(Player player, String[] args) {

        if(args.length != 3) {  return true; }
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[1]);

        if(offlinePlayer == null) {
            Main.util.sendMessage(player, MessagePath.PlayerNotFound.getPath());
            return true;
        }

        try {
            double amount = Double.parseDouble(args[2]);

            if(amount < 0) {
                Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                return true;
            }

            MoneyTable moneyTable = new MoneyTable(plugin);

            moneyTable.getPlayerData(args[1]).thenAccept(result -> {

                try {
                    double currentBalance = result.getDouble("money");

                    moneyTable.setMoney(args[1], currentBalance).thenAccept(success -> {
                        Main.util.sendMessage(player, MessagePath.MoneySetPlayer.getPath()
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                                .replace("#target#", args[1])
                                .replace("#amount#", Main.util.formatDouble(currentBalance))
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

        return false;
    }
}
