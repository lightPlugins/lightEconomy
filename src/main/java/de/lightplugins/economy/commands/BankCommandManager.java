package de.lightplugins.economy.commands;

import de.lightplugins.economy.commands.bank.*;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.inventories.BankMainMenu;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class BankCommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    public Main plugin;

    public BankCommandManager(Main plugin) {
        this.plugin = plugin;
        subCommands.add(new BankMenuCommand());
        subCommands.add(new BankAddCommand());
        subCommands.add(new BankSetCommand());
        subCommands.add(new BankSetLevelCommand());
        subCommands.add(new BankRemoveCommand());
        subCommands.add(new BankShowCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(sender instanceof Player) {
            Player player = (Player) sender;

            FileConfiguration config = Main.settings.getConfig();

            if(!config.getBoolean("settings.enable-bank-feature")) {
                Main.util.sendMessage(player, MessagePath.BankDisabled.getPath());
                return false;
            }

            if (args.length > 0) {
                for(int i = 0; i < subCommands.size(); i++) {
                    if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {

                        try {
                            if(getSubCommands().get(i).perform(player, args)) {
                                Main.debugPrinting.sendInfo("MainSubCommand " + Arrays.toString(args) + " successfully executed by " + player.getName());
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException("Something went wrong", e);
                        }
                    }
                }
            } else {

                if(player.hasPermission(PermissionPath.BankOpen.getPerm())) {
                    BankMainMenu.INVENTORY.open(player);
                    return false;
                }

                Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                return false;
            }
        }

        return false;
    }
}
