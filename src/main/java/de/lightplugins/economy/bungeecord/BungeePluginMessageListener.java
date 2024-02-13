package de.lightplugins.economy.bungeecord;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class BungeePluginMessageListener implements PluginMessageListener {


    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String command = input.readUTF();
        String args1 = input.readUTF();

        player.sendMessage("SUCCESS: " + args1);

        Bukkit.getConsoleSender().sendMessage("Pluginmessage empfangen: CMD:" + command + " Arg1:" + args1);


    }
}
