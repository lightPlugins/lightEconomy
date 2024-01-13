package de.lightplugins.economy.commands.tabcompletion;

import de.lightplugins.economy.enums.PermissionPath;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MoneyTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {


        if(args.length == 1) {
            List<String> arguments = new ArrayList<>();
            if(sender.hasPermission(PermissionPath.MoneyAdd.getPerm())) { arguments.add("add"); }
            if(sender.hasPermission(PermissionPath.MoneyAddAll.getPerm())) { arguments.add("addall"); }
            if(sender.hasPermission(PermissionPath.MoneyRemove.getPerm())) { arguments.add("remove"); }
            if(sender.hasPermission(PermissionPath.MoneySet.getPerm())) { arguments.add("set"); }
            if(sender.hasPermission(PermissionPath.MoneyTop.getPerm())) { arguments.add("top");  }
            if(sender.hasPermission(PermissionPath.MoneyOther.getPerm())) { arguments.add("show"); }
            if(sender.hasPermission(PermissionPath.CreateVoucher.getPerm())) { arguments.add("voucher create"); }

            return arguments;
        }

        return null;
    }
}
