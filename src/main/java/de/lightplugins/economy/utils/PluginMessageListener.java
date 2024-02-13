package de.lightplugins.economy.utils;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.lightplugins.economy.enums.PluginMessagePath;
import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {


    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String command = input.readUTF();

        //  if channel messaging not match the target endpoint
        if(!command.equalsIgnoreCase(PluginMessagePath.PAY.getType())) {
            return;
        }

        String targetName = input.readUTF();
        String args1 = input.readUTF();

        Bukkit.getLogger().log(Level.WARNING,"target: " + targetName);
        Bukkit.getLogger().log(Level.WARNING,"args: " + args1);
        Bukkit.getLogger().log(Level.WARNING,"channel: " + channel);

        Player target = Bukkit.getPlayer(targetName);

        //  if target is null
        if(target == null) {
            Bukkit.getConsoleSender().sendMessage("Target is null");
            return;
        }

        Main.util.sendMessage(target, args1);

    }
}
