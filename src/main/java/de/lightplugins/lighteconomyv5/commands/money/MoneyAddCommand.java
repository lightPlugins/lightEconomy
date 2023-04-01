package de.lightplugins.lighteconomyv5.commands.money;

import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.enums.PermissionPath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


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

            // TODO: insert error message here, if something went wrong

        } catch (NumberFormatException notANumber) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
        }
        return false;
    }
}
