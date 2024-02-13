package de.lightplugins.economy.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;

public class Bungee extends Plugin {

    public static Bungee getInstance;

    @Override
    public void onEnable() {
        getLogger().info("Yay! It loads!");

        getInstance = this;

        getProxy().registerChannel("lighteconomy:messages");
        getProxy().getPluginManager().registerListener(this, new ChannelListener());
    }
}
