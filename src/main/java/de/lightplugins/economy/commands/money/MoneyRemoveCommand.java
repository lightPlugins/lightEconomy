package de.lightplugins.economy.commands.money;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

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


        if(args.length != 3) {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            return true;
        }

        if(!Main.economyImplementer.hasAccount(args[1])) {
            Main.util.sendMessage(player, MessagePath.PlayerNotExists.getPath());
            return false;
        }

        if(!player.hasPermission(PermissionPath.MoneyRemove.getPerm())) {
            Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
            return false;
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

            double currentBalance = Main.economyImplementer.getBalance(args[1]);

            if(amount >= currentBalance) {
                amount = currentBalance;
            }

            EconomyResponse moneyRemove = Main.economyImplementer.withdrawPlayer(args[1], amount);
            if(moneyRemove.transactionSuccess()) {
                Main.util.sendMessage(player, MessagePath.MoneyRemovePlayer.getPath()
                        .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                        .replace("#target#", args[1])
                        .replace("#amount#", Main.util.formatDouble(amount)));
                return true;

            }

        } catch (NumberFormatException e) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
            return true;
        }


        return false;
    }
}
