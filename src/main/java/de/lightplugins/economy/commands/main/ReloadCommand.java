package de.lightplugins.economy.commands.main;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "reloads the plugin";
    }

    @Override
    public String getSyntax() {
        return "/le reload";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length != 1)  {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            return false;
        }

        if(!player.hasPermission(PermissionPath.Reload.getPerm())) {
            Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
            return false;
        }

        Main.messages.reloadConfig("messages.yml");
        Main.titles.reloadConfig("titles.yml");

        Main.util.sendMessage(player, MessagePath.Reload.getPath());

        return false;
    }
}
