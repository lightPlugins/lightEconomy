package de.lightplugins.economy.bungeecord;

import de.lightplugins.economy.enums.PluginMessagePath;
import net.md_5.bungee.api.plugin.Plugin;

public class Bungee extends Plugin {

    public static Bungee getInstance;

    /**
     * This method is called when the plugin is enabled
     */
    @Override
    public void onEnable() {

        // Display a welcome message in the console
        getProxy().getConsole().sendMessage("\n " +
                " §r_      _____ _____ _    _ _______ §c______ _____ ____  _   _  ____  __  ____     __\n" +
                " §r| |    |_   _/ ____| |  | |__   __§c|  ____/ ____/ __ \\| \\ | |/ __ \\|  \\/  \\ \\   / /\n" +
                " §r| |      | || |  __| |__| |  | |  §c| |__ | |   | |  | |  \\| | |  | | \\  / |\\ \\_/ / \n" +
                " §r| |      | || | |_ |  __  |  | |  §c|  __|| |   | |  | | . ` | |  | | |\\/| | \\   /  \n" +
                " §r| |____ _| || |__| | |  | |  | |  §c| |___| |___| |__| | |\\  | |__| | |  | |  | |   \n" +
                " §r|______|_____\\_____|_|  |_|  |_|  §c|______\\_____\\____/|_| \\_|\\____/|_|  |_|  |_|" +
                "\n\n" + "§r" +
                "      Version: §c5.5.0   §rAuthor: §clightPlugins\n" +
                "      §rThank you for using lightEconomy on Proxy. If you came in trouble feel free to join\n" +
                "      my §cDiscord §rserver: https://discord.gg/G2EuzmSW\n" +
                "      Make sure you changed on each subserver in the settings.yml §cbungeecord: true\n");

        // Set the plugin instance
        getInstance = this;

        // Register a channel for plugin messages
        getProxy().registerChannel(PluginMessagePath.PAY.getType());

        // Register a listener for plugin channel messages
        getProxy().getPluginManager().registerListener(this, new ChannelListener());
    }
}
