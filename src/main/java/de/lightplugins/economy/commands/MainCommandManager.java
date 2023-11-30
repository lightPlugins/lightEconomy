package de.lightplugins.economy.commands;

import de.lightplugins.economy.commands.main.DebugCommand;
import de.lightplugins.economy.commands.main.HelpCommand;
import de.lightplugins.economy.commands.main.ReloadCommand;
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

public class MainCommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }


    public Main plugin;
    public MainCommandManager(Main plugin) {
        this.plugin = plugin;
        subCommands.add(new HelpCommand());
        subCommands.add(new ReloadCommand());
        //subCommands.add(new VoucherCommand());
        subCommands.add(new DebugCommand());
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
                            throw new RuntimeException("Something went wrong in executing " + Arrays.toString(args), e);
                        }
                    }
                }
            } else {

                Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            }
        }

        return false;
    }
}
