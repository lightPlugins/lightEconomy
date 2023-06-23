package de.lightplugins.economy.commands.bank;

import de.lightplugins.economy.inventories.BankMainMenu;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class BankMenuCommand extends SubCommand {
    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public String getDescription() {
        return "Opens the bank Inventory";
    }

    @Override
    public String getSyntax() {
        return "/bank";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        BankMainMenu.INVENTORY.open(player);

        return false;
    }
}
