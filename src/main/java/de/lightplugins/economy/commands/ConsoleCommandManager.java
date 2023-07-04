package de.lightplugins.economy.commands;

import de.lightplugins.economy.commands.console.*;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ConsoleCommandManager implements CommandExecutor {


    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }


    public Main plugin;
    public ConsoleCommandManager(Main plugin) {
        this.plugin = plugin;
        subCommands.add(new MoneyAddConsole());
        subCommands.add(new MoneyRemoveConsole());
        subCommands.add(new MoneySetConsole());
        subCommands.add(new PluginReloadConsole());
        subCommands.add(new MoneyReset());
        subCommands.add(new BankOpenConsole());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {


        if(sender instanceof ConsoleCommandSender) {

            if (args.length > 0) {
                for(int i = 0; i < subCommands.size(); i++) {
                    if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                        try {
                            if(getSubCommands().get(i).perform(null, args)) { return false; }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            Main.util.sendMessage((Player) sender, MessagePath.OnlyConsole.getPath());
        }

        return false;
    }
}
