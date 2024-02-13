package de.lightplugins.economy.bungeecord;


import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class ChannelListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e){

        System.out.println(new String(e.getData()));
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));

        try {
            String channel = in.readUTF();
            String input = in.readUTF(); // the inputstring 1 - player
            String input2 = in.readUTF(); // the inputstring 2 - data (message)
            ServerInfo server = Bungee.getInstance.getProxy().getPlayer(e.getReceiver().toString()).getServer().getInfo();
            sendToBukkit(channel, input2, server);

            System.out.println("TEST: " + channel + " input " + input + " input2: " + input2);
        } catch (IOException ex) {
            // throw new RuntimeException(ex);
        }
    }

    public void sendToBukkit(String channel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.sendData("lighteconomy:messages", stream.toByteArray());

    }
}
