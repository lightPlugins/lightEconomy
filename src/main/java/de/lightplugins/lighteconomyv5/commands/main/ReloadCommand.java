package de.lightplugins.lighteconomyv5.commands.main;

import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.enums.PermissionPath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
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

        if(args.length != 1)  { return false; }

        if(!player.hasPermission(PermissionPath.Reload.getPerm())) {
            Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
            return false;
        }

        Main.messages.reloadConfig("messages.yml");
        Main.messages.reloadConfig("titles.yml");

        Main.util.sendMessage(player, MessagePath.Reload.getPath());

        return false;
    }
}
