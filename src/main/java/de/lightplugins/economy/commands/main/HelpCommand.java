package de.lightplugins.economy.commands.main;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class HelpCommand extends SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Get an overview of all possible Commands";
    }

    @Override
    public String getSyntax() {
        return "/le help";
    }

    @Override
    public boolean perform(Player player, String[] args) {

        if(args.length == 1) {
            FileConfiguration messages = Main.messages.getConfig();

            if(player.hasPermission(PermissionPath.HelpCommandAdmin.getPerm())) {
                for(String s : messages.getStringList("helpCommandAdmin")) {
                    player.sendMessage(Main.colorTranslation.hexTranslation(s));
                }
                return false;
            }

            for(String s : messages.getStringList("helpCommandPlayer")) {
                player.sendMessage(Main.colorTranslation.hexTranslation(s));
            }

            return false;
        }
        Main.util.sendMessage(player, MessagePath.WrongCommand.getPath()
                .replace("#command#", getSyntax()));
        return false;
    }
}
