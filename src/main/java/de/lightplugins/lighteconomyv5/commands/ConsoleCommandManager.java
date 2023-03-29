package de.lightplugins.lighteconomyv5.commands;

import de.lightplugins.lighteconomyv5.commands.console.MoneyAddConsole;
import de.lightplugins.lighteconomyv5.commands.console.MoneyRemoveConsole;
import de.lightplugins.lighteconomyv5.commands.console.MoneySetConsole;
import de.lightplugins.lighteconomyv5.commands.main.HelpCommand;
import de.lightplugins.lighteconomyv5.commands.main.StatusCommand;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.Console;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(sender instanceof ConsoleCommandSender) {

            if (args.length > 0) {
                for(int i = 0; i < subCommands.size(); i++) {
                    if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                        try {
                            if(getSubCommands().get(i).perform(null, args)) {
                                Bukkit.getLogger().log(Level.INFO, "MainSubCommand successfully executed!");
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {

                /* if the Main command is /money, just do here a quick balance checkout */
            }
        }

        return false;
    }
}
