package de.lightplugins.economy.commands.money;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;


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


        if(args.length != 3) {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            return true;
        }

        if(!Main.economyImplementer.hasAccount(args[1])) {
            Main.util.sendMessage(player, MessagePath.PlayerNotExists.getPath());
            return false;
        }

        if(!player.hasPermission(PermissionPath.MoneyAdd.getPerm())) {
            Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
            return false;
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


            EconomyResponse moneyAdd = Main.economyImplementer.depositPlayer(args[1], amount);
            if(moneyAdd.transactionSuccess()) {
                Main.util.sendMessage(player, MessagePath.MoneyAddPlayer.getPath()
                        .replace("#amount#", Main.util.formatDouble(amount))
                        .replace("#target#", args[1])
                        .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                        .replace("#balance#", Main.util.formatDouble(amount))); // TODO: richtige Zahl ausgeben
                return true;

            }

            /*
                ERROR Message if something Wrong !
             */

            Bukkit.getLogger().log(Level.WARNING, "ERROR: " + moneyAdd.errorMessage);

            // TODO: insert error message here, if something went wrong

        } catch (NumberFormatException notANumber) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
        }
        return false;
    }
}
