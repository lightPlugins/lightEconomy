package de.lightplugins.economy.commands.money;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class MoneyShowCommand extends SubCommand {
    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "shows other players balance";
    }

    @Override
    public String getSyntax() {
        return "/money show [playername]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(!player.hasPermission(PermissionPath.MoneyOther.getPerm())) {
            Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
            return false;
        }

        if(args.length != 2) {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            return true;
        }

        if(!Main.economyImplementer.hasAccount(args[1])) {
            Main.util.sendMessage(player, MessagePath.PlayerNotExists.getPath());
            return false;
        }

        try {

            MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

            double balanceTarget = moneyTableAsync.playerBalance(args[1]).get();

            Main.util.sendMessage(player, MessagePath.MoneyBalanceOther.getPath()
                    .replace("#target#", args[1])
                    .replace("#balance#", Main.util.finalFormatDouble(balanceTarget))
                    .replace("#currency#", Main.util.getCurrency(balanceTarget)));


        }catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Something went wrong on player show money", e);
        }

        return false;
    }
}
