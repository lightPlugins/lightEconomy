package de.lightplugins.lighteconomyv5.commands.main;

import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
import org.bukkit.entity.Player;

public class MainSubCommand extends SubCommand {

    public Main plugin;
    public MainSubCommand(Main plugin) {this.plugin = plugin; }


    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Show the status about lightEconomy V5";
    }

    @Override
    public String getSyntax() {
        return "/le status";
    }

    @Override
    public boolean perform(Player player, String[] args) {

        if(args.length == 1) {
            player.sendMessage("Plugin is ready to use!");
        }
        return false;
    }
}
