package de.lightplugins.economy.bungeecord;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeePluginMessageListener implements PluginMessageListener {


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        if (player == null) {
            // Nachricht wurde von der Konsole gesendet
            // Verarbeite die Nachricht entsprechend
        } else {
            // Nachricht wurde von einem Spieler gesendet
            // Verarbeite die Nachricht entsprechend
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String messageType = in.readUTF();
        String data = in.readUTF();

        // Hier verarbeite die empfangene Nachricht
        // Beispiel: System.out.println("Nachrichtentyp: " + messageType + ", Daten: " + data);
    }

}
