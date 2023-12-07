package de.lightplugins.economy.commands.console;

import de.lightplugins.economy.inventories.BankMainMenu;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class BankOpenConsole extends SubCommand {
    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public String getDescription() {
        return "bank commands from console";
    }

    @Override
    public String getSyntax() {
        return "eco bank open <player>";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 3) {

            Player target = Bukkit.getPlayer(args[2]);
            if(args[1].equalsIgnoreCase("open")) {

                if(target == null) {
                    Bukkit.getConsoleSender().sendMessage(
                            Main.consolePrefix + "The Player was not found on this server");
                    return false;
                }

                if(target.isOnline()) {
                    BankMainMenu.INVENTORY.open(target);
                    return false;
                }
                return false;
            }

            Bukkit.getConsoleSender().sendMessage(
                    Main.consolePrefix + "Wrong command. Please use /eco bank open PLAYERNAME");
            return false;
        }

        Bukkit.getConsoleSender().sendMessage(
                Main.consolePrefix + "Wrong command. Please use /eco bank open PLAYERNAME");
        return false;
    }
}
