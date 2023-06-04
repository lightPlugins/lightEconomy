package de.lightplugins.economy.commands.console;

import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class MoneyReset extends SubCommand {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "delete a player from the database";
    }

    @Override
    public String getSyntax() {
        return "/eco reset [PLAYERNAME]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length != 2)  {

        }

        return false;
    }
}
