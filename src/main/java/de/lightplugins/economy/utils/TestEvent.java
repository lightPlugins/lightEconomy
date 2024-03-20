package de.lightplugins.economy.utils;

import de.lightplugins.economy.api.enums.TransactionStatus;
import de.lightplugins.economy.api.events.LightEcoWithdrawPocketEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class TestEvent implements Listener {

    @EventHandler
    public void onTest(LightEcoWithdrawPocketEvent event) {

        Bukkit.getLogger().log(Level.INFO, "Accountname: " + event.getTargetAccountName());
        Bukkit.getLogger().log(Level.INFO, "Amount: " + event.getAmount());
        Bukkit.getLogger().log(Level.INFO, "IsCancelled: " + event.isCancelled());
        Bukkit.getLogger().log(Level.INFO, "Status: " + event.getTransactionStatus());

        if(event.getTransactionStatus().equals(TransactionStatus.SUCCESS)) {
            // do something if the event is succeded
        }

        double newAmount = event.getAmount((status, amount) -> {
            if (status == TransactionStatus.SUCCESS) {
                return amount + event.setAmount(400);
            }
            return event.getAmount();
        });

        Bukkit.getLogger().log(Level.INFO, "Amount2: " + newAmount);

    }
}
