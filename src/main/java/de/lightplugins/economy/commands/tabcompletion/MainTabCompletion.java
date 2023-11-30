package de.lightplugins.economy.commands.tabcompletion;

import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        Player player = (Player) sender;

        if(args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("help");

            if(player.hasPermission("lighteconomy.admin.command.reload")) {
                arguments.add("reload");
            }
            if(player.hasPermission("lighteconomy.admin.command.debug")) {
                arguments.add("debug");
            }
            if(player.hasPermission(PermissionPath.CreateNPC.getPerm())) {
                if(Main.isCitizens) {
                    arguments.add("createnpc");
                }
            }

            return arguments;
        }

        return null;
    }
}
