package de.lightplugins.economy.commands;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class BalanceCommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }


    public Main plugin;
    public BalanceCommandManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                for(int i = 0; i < subCommands.size(); i++) {
                    if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {

                        try {
                            if(getSubCommands().get(i).perform(player, args)) {
                                Main.debugPrinting.sendInfo("MainSubCommand " + Arrays.toString(args) + " successfully executed by " + player.getName());
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                MoneyTableAsync moneyTableAsync = new MoneyTableAsync(plugin);
                moneyTableAsync.playerBalance(player.getName()).thenAccept(balance -> {
                    if(balance != null) {
                        double currentBalance = balance;
                        Main.util.sendMessage(player, MessagePath.MoneyBalance.getPath()
                                .replace("#balance#", Main.util.finalFormatDouble(currentBalance))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    } else {
                        player.sendMessage(Main.colorTranslation.hexTranslation(MessagePath.Prefix.getPath()
                                + MessagePath.PlayerNotFound.getPath()));
                    }

                });
            }

            if(args.length == 1) {

                if(!player.hasPermission(PermissionPath.MoneyOther.getPerm())) {
                    Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                    return false;
                }

                MoneyTableAsync moneyTableAsync = new MoneyTableAsync(plugin);
                moneyTableAsync.playerBalance(args[0]).thenAccept(balance -> {
                    if(balance != null) {
                        double currentBalance = balance;
                        Main.util.sendMessage(player, MessagePath.MoneyBalance.getPath()
                                .replace("#balance#", Main.util.finalFormatDouble(currentBalance))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    } else {
                        player.sendMessage(Main.colorTranslation.hexTranslation(MessagePath.Prefix.getPath()
                                + MessagePath.PlayerNotFound.getPath()));
                    }

                });
            }
        }

        return false;
    }
}
