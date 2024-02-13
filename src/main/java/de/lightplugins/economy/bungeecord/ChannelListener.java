package de.lightplugins.economy.bungeecord;


import de.lightplugins.economy.enums.PluginMessagePath;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class ChannelListener implements Listener {

    @EventHandler
    public void receivePayMessageChannel(PluginMessageEvent e){

        System.out.println(new String(e.getData()));
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));

        try {
            String channel = in.readUTF();
            // lighteconomy:pay
            if(!channel.equalsIgnoreCase(PluginMessagePath.PAY.getType())) {
                return;
            }

            String targetName = in.readUTF(); // the inputstring 1 - player
            String input2 = in.readUTF(); // the inputstring 2 - data (message)
            ProxiedPlayer proxiedPlayer = Bungee.getInstance.getProxy().getPlayer(targetName);

            if(proxiedPlayer == null) {
                return;
            }

            ServerInfo server = proxiedPlayer.getServer().getInfo();
            sendToBukkit(channel, input2, targetName, server);
        } catch (IOException ex) {
            // throw new RuntimeException(ex);
        }
    }

    public void sendToBukkit(String channel, String message, String targetName, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(targetName);
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.sendData(PluginMessagePath.PAY.getType(), stream.toByteArray());

    }
}
