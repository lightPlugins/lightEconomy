package de.lightplugins.economy.placeholder;

import de.lightplugins.economy.master.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lighteconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "lightPlugins";
    }

    @Override
    public @NotNull String getVersion() {
        return "5.0.3";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if(params.equalsIgnoreCase("money")) {
            double amount = Main.util.fixDouble(Main.economyImplementer.getBalance(player.getName()));
            return Main.util.formatDouble(amount);
        }
        if(params.equalsIgnoreCase("currency_singular")) {
            return Main.economyImplementer.currencyNameSingular();
        }
        if(params.equalsIgnoreCase("currency_plural")) {
            return Main.economyImplementer.currencyNamePlural();
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
