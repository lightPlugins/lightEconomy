package de.lightplugins.lighteconomyv5.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {


        if(args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("status");

            return arguments;
        }



        return null;
    }
}
