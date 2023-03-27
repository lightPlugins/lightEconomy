package de.lightplugins.lighteconomyv5.commands;

import de.lightplugins.lighteconomyv5.commands.money.MoneyAddCommand;
import de.lightplugins.lighteconomyv5.commands.money.MoneyRemoveCommand;
import de.lightplugins.lighteconomyv5.commands.money.MoneySetCommand;
import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

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
                                Bukkit.getLogger().log(Level.INFO, "MainSubCommand successfully executed!");
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
                        player.sendMessage(Main.colorTranslation.hexTranslation(MessagePath.Prefix.getPath()
                                        + MessagePath.MoneyBalance.getPath())
                                .replace("#balance#", Main.util.formatDouble(currentBalance))
                                .replace("#currency#", Main.currencyName));
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
