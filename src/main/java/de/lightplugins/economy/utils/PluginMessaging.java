package de.lightplugins.economy.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.lightplugins.economy.master.Main;
import org.bukkit.entity.Player;

public class PluginMessaging {

    /**
     * Sends a message through the Bungee network.
     *
     * @param channelType the type of channel to send the message to
     * @param player the player who is sending the message
     * @param message the message to be sent
     */
    public void sendMessageThrowBungeeNetwork(String channelType, Player player, String message) {

        // Create a new data output stream
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Write the channel type, player name, and message to the data output stream
        out.writeUTF(channelType);
        out.writeUTF(player.getName());
        out.writeUTF(Main.colorTranslation.hexTranslation(message));

        // Send the plugin message through the BungeeCord channel
        player.sendPluginMessage(Main.getInstance, "BungeeCord", out.toByteArray());

    }
}
