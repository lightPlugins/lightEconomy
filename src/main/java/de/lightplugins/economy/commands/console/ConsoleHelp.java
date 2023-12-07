package de.lightplugins.economy.commands.console;

import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class ConsoleHelp extends SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows the help syntax from console";
    }

    @Override
    public String getSyntax() {
        return "/eco help";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) {

                Bukkit.getConsoleSender().sendMessage("\n " +
                        " §r_      _____ _____ _    _ _______ §c______ _____ ____  _   _  ____  __  ____     __\n" +
                        " §r| |    |_   _/ ____| |  | |__   __§c|  ____/ ____/ __ \\| \\ | |/ __ \\|  \\/  \\ \\   / /\n" +
                        " §r| |      | || |  __| |__| |  | |  §c| |__ | |   | |  | |  \\| | |  | | \\  / |\\ \\_/ / \n" +
                        " §r| |      | || | |_ |  __  |  | |  §c|  __|| |   | |  | | . ` | |  | | |\\/| | \\   /  \n" +
                        " §r| |____ _| || |__| | |  | |  | |  §c| |___| |___| |__| | |\\  | |__| | |  | |  | |   \n" +
                        " §r|______|_____\\_____|_|  |_|  |_|  §c|______\\_____\\____/|_| \\_|\\____/|_|  |_|  |_|" +
                        "\n\n" + ChatColor.RESET +
                        "      Available console commands:\n\n" +
                        "      §ceco help §r- show the command list\n" +
                        "      §ceco open [playername] §r- opens the bank menu for target\n" +
                        "      §ceco add [playername] [amount] §r- add a certant amount of money to player\n" +
                        "      §ceco remove [playername] [amount] §r- remove a certant amount of money to player\n" +
                        "      §ceco set [playername] [amount] §r- set a certant amount of money to player\n" +
                        "      §ceco reset [playername] §r- delete a player in the database. The player will be kicked and must rejoin\n" +
                        "      §ceco reload §r- reloads the configs\n" +
                        "\n");
                return false;
            }

            Bukkit.getConsoleSender().sendMessage( "This command does not exist. Please try /eco reload");
            return false;
        }

        Bukkit.getConsoleSender().sendMessage(
                Main.consolePrefix + "Wrong command. Please use /eco reload");
        return false;
    }
}
