package de.lightplugins.economy.utils;

import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reflections {


    private Main plugin;

    public Reflections(Main plugin) {
        this.plugin = plugin;
    }


    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception ex) {
            throw new RuntimeException("ERROR", ex);
        }
    }

    public  Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("ERROR", ex);
        }
    }
}
