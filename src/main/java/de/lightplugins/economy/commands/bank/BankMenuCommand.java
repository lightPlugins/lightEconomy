package de.lightplugins.economy.commands.bank;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.inventories.BankMainMenu;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class BankMenuCommand extends SubCommand {
    @Override
    public String getName() {
        return "open";
    }

    @Override
    public String getDescription() {
        return "Opens the bank Inventory";
    }

    @Override
    public String getSyntax() {
        return "/bank open [player]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 2) {

            if(!player.hasPermission(PermissionPath.BankOpenOther.getPerm())) {
                Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                Main.debugPrinting.sendWarning("Target player from /bank open [target] not found!");
                return false;
            }

            BankMainMenu.INVENTORY.open(target);
        }

        return false;
    }
}
