package de.lightplugins.economy.discord;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.lightplugins.economy.master.Main;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class DiscordBot extends Thread {

    private String token;

    public void DiscordBotThread(String token) {
        this.token = token;
    }

    @Override
    public void run() {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
    }

    public void test() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("DeinNachrichtentyp");
        out.writeUTF("DeineDaten");

        Server server = Bukkit.getServer(); // Zugriff auf die Serverinstanz
        server.sendPluginMessage(Main.getInstance, "BungeeCord", out.toByteArray());

    }


}
