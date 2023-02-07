package de.lightplugins.lighteconomyv5.hooks;

import de.lightplugins.lighteconomyv5.master.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.util.logging.Level;

public class VaultHook {

    private Main plugin = Main.getInstance;
    private Economy provider;

    public void hook() {
        provider = plugin.economyImplementer;
        Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Highest);
        Bukkit.getLogger().log(Level.INFO,
                "Vault successfully hooked with highest priority into " + plugin.getName());
    }

    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        Bukkit.getLogger().log(Level.INFO,
                "Vault successfully unhooked from " + plugin.getName());
    }

}
