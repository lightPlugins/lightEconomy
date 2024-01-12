package de.lightplugins.economy.commands.money;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.beans.ExceptionListener;
import java.util.concurrent.ExecutionException;
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

        FileConfiguration settings = Main.settings.getConfig();
        double maxPocketBalance = settings.getDouble("settings.max-pocket-balance");

        if(args.length != 3) {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath()
                    .replace("#command#", getSyntax()));
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

            MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

            try {

                double amount = Double.parseDouble(args[2]);

                double playerBalance = moneyTableAsync.playerBalance(args[1]).get();

                if((playerBalance + amount) > maxPocketBalance) {
                    Main.util.sendMessage(player, MessagePath.TransactionFailed.getPath()
                            .replace("#reason#", "This value reached the max pocket balance of "
                                    + maxPocketBalance + "!"));
                    return false;
                }

                if(amount == 0) {
                    Main.util.sendMessage(player, MessagePath.NotZero.getPath()
                            .replace("#min-amount#", "0.01")
                            .replace("#currency#", Main.util.getCurrency(0)));
                    return true;
                }

                if(amount < 0) {
                    Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                    return true;
                }


                EconomyResponse moneyAdd = Main.economyImplementer.depositPlayer(args[1], amount);
                if(moneyAdd.transactionSuccess()) {
                    Main.util.sendMessage(player, MessagePath.MoneyAddPlayer.getPath()
                            .replace("#amount#", Main.util.finalFormatDouble(amount))
                            .replace("#target#", args[1])
                            .replace("#currency#", Main.util.getCurrency(amount))
                            .replace("#balance#", Main.util.finalFormatDouble(playerBalance + amount)));
                    return true;

                }

            /*
                ERROR Message if something Wrong !
             */

                Main.debugPrinting.sendError(moneyAdd.errorMessage);

                // TODO: insert error message here, if something went wrong

            }catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("Something went wrong on player add money", e);
            }


        } catch (NumberFormatException notANumber) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
        }
        return false;
    }
}
