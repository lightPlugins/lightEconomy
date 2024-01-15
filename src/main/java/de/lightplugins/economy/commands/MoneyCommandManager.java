package de.lightplugins.economy.commands;

import de.lightplugins.economy.commands.money.MoneyVoucherCommand;
import de.lightplugins.economy.commands.money.*;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MoneyCommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }


    public Main plugin;
    public MoneyCommandManager(Main plugin) {

        this.plugin = plugin;
        subCommands.add(new MoneyAddCommand());
        subCommands.add(new MoneyRemoveCommand(plugin));
        subCommands.add(new MoneySetCommand(plugin));
        subCommands.add(new MoneyTopCommand());
        subCommands.add(new MoneyShowCommand());
        subCommands.add(new MoneyAddAllCommand());
        subCommands.add(new MoneyVoucherCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(sender instanceof Player) {
            Player player = (Player) sender;

            boolean contains = false;

            if (args.length > 0) {
                for(int i = 0; i < subCommands.size(); i++) {
                    if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {

                        try {
                            if(getSubCommands().get(i).perform(player, args)) {
                                contains = true;
                                Main.debugPrinting.sendInfo("MainSubCommand " + Arrays.toString(args) + " successfully executed by " + player.getName());
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException("Something went wrong while executing MoneycommandManager", e);
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
                                .replace("#currency#", Main.util.getCurrency(currentBalance)));
                    } else {
                        player.sendMessage(Main.colorTranslation.hexTranslation(MessagePath.Prefix.getPath()
                                + MessagePath.PlayerNotFound.getPath()));
                    }

                });

                return false;
            }
        }
        return false;
    }

}
