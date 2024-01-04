package de.lightplugins.economy.hooks;

import de.lightplugins.economy.master.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.util.logging.Level;

public class VaultHook {

    private final Main plugin = Main.getInstance;
    private Economy provider;

    public void hook() {
        this.provider = Main.economyImplementer;
        Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Highest);
        Bukkit.getConsoleSender().sendMessage(Main.consolePrefix +
                "Vault successfully hooked with highest priority into " + plugin.getName());
    }

    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        Bukkit.getConsoleSender().sendMessage(Main.consolePrefix +
                "Vault successfully unhooked from " + plugin.getName());
    }

}
