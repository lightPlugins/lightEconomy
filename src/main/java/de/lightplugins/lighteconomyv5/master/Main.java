package de.lightplugins.lighteconomyv5.master;

import de.lightplugins.lighteconomyv5.commands.MainCommandManager;
import de.lightplugins.lighteconomyv5.commands.MainTabCompletion;
import de.lightplugins.lighteconomyv5.hooks.VaultHook;
import de.lightplugins.lighteconomyv5.implementer.EconomyImplementer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Main getInstance;
    public EconomyImplementer economyImplementer;
    private VaultHook vaultHook;
    public Economy econ;

    public void onLoad() {

        /*  Initialize the Plugins instance  */

        getInstance = this;

        economyImplementer = new EconomyImplementer();
        vaultHook = new VaultHook();
        vaultHook.hook();
        Bukkit.getLogger().log(Level.INFO, "Successfully loaded " + this.getName());
    }

    public void onEnable() {
        Bukkit.getLogger().log(Level.INFO, "Successfully started " + this.getName());

        Objects.requireNonNull(this.getCommand("le")).setExecutor(new MainCommandManager(this));
        Objects.requireNonNull(this.getCommand("le")).setTabCompleter(new MainTabCompletion());
    }

    public void onDisable() {
        vaultHook.unhook();
        Bukkit.getLogger().log(Level.INFO, "Successfully stopped " + this.getName());
    }
}
