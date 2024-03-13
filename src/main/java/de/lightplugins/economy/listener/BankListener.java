package de.lightplugins.economy.listener;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.BankLevelSystem;
import de.lightplugins.economy.utils.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BankListener implements Listener {

    private Main plugin;
    public BankListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onChatBankWithdraw(AsyncPlayerChatEvent e) {

        Player chatter = e.getPlayer();
        String message = e.getMessage();

        if(plugin.bankWithdrawValue.contains(chatter)) {
            e.setCancelled(true);
        } else {
            return;
        }

        if(message.equalsIgnoreCase("cancel")) {
            plugin.bankWithdrawValue.remove(chatter);
            return;
        }

        plugin.bankWithdrawValue.remove(chatter);

        MoneyTableAsync moneyTable = new MoneyTableAsync(plugin);
        BankTableAsync bankTable = new BankTableAsync(plugin);
        Sounds sounds = new Sounds();

        try {

            double amount = Double.parseDouble(message);
            double pocketAmount;
            double bankAmount;

            CompletableFuture<Double> futurePocket = moneyTable.playerBalance(chatter.getName());
            CompletableFuture<Double> futureBank = bankTable.playerBankBalance(chatter.getName());

            try {
                pocketAmount = futurePocket.get();
                bankAmount = futureBank.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }

            if(amount <= 0) {
                Main.util.sendMessage(chatter, MessagePath.OnlyPositivNumbers.getPath());
                sounds.soundOnFailure(chatter);
                plugin.bankDepositValue.remove(chatter);
                return;
            }

            if(amount > bankAmount) {
                Main.util.sendMessage(chatter, MessagePath.BankWithdrawNotEnough.getPath());
                sounds.soundOnFailure(chatter);
                return;
            }


            if(amount <= bankAmount) {

                CompletableFuture<Boolean> completableFuture =
                        moneyTable.setMoney(chatter.getName(), pocketAmount + amount);
                CompletableFuture<Boolean> completableFuture1 =
                        bankTable.setBankMoney(chatter.getName(), bankAmount - amount);

                try {

                    if(completableFuture1.get() && completableFuture.get()) {

                        Main.util.sendMessage(chatter, MessagePath.BankWithdrawSuccessfully.getPath()
                                .replace("#amount#", String.valueOf(amount))
                                .replace("#currency#", Main.util.getCurrency(amount)));
                        sounds.soundOnSuccess(chatter);


                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }


        } catch (NumberFormatException ex) {
            Main.util.sendMessage(chatter, MessagePath.NotANumber.getPath());
            sounds.soundOnFailure(chatter);
            throw new RuntimeException("Not a Number Exception: Please send this error to lightPlugins", ex);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onChatBankDeposit(AsyncPlayerChatEvent e) {

        Player chatter = e.getPlayer();
        String message = e.getMessage();

        if (plugin.bankDepositValue.contains(chatter)) {
            e.setCancelled(true);
        } else {
            return;
        }

        if (message.equalsIgnoreCase("cancel")) {
            if (plugin.bankDepositValue.contains(chatter)) {
                plugin.bankDepositValue.remove(chatter);
                return;
            }
        }

        plugin.bankDepositValue.remove(chatter);

        try {

            double amount = Double.parseDouble(message);

            BankLevelSystem bankLevelSystem = new BankLevelSystem(plugin);
            Sounds sounds = new Sounds();

            MoneyTableAsync moneyTable = new MoneyTableAsync(plugin);
            BankTableAsync bankTable = new BankTableAsync(plugin);

            double pocketAmount;
            double bankAmount;

            CompletableFuture<Double> futurePocket = moneyTable.playerBalance(chatter.getName());
            CompletableFuture<Double> futureBank = bankTable.playerBankBalance(chatter.getName());

            try {
                pocketAmount = futurePocket.get();
                bankAmount = futureBank.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }

            if(amount <= 0) {
                Main.util.sendMessage(chatter, MessagePath.OnlyPositivNumbers.getPath());
                sounds.soundOnFailure(chatter);
                plugin.bankDepositValue.remove(chatter);
                return;
            }


            if(bankAmount == bankLevelSystem.getLimitByLevel(chatter.getUniqueId())) {
                Main.util.sendMessage(chatter, MessagePath.BankDepositNotPossible.getPath());
                sounds.soundOnFailure(chatter);
                plugin.bankDepositValue.remove(chatter);
                return;

            }

            if(amount > bankLevelSystem.getLimitByLevel(chatter.getUniqueId())) {
                Main.util.sendMessage(chatter, MessagePath.BankDepositOverLimit.getPath());
                sounds.soundOnFailure(chatter);
                plugin.bankDepositValue.remove(chatter);
                return;
            }

            if(bankAmount + amount >= bankLevelSystem.getLimitByLevel(chatter.getUniqueId())) {
                Main.util.sendMessage(chatter, MessagePath.BankDepositOverLimit.getPath());
                sounds.soundOnFailure(chatter);
                plugin.bankDepositValue.remove(chatter);
                return;
            }

            double currentBankBalance = bankAmount;

            if (amount <= pocketAmount) {

                CompletableFuture<Boolean> completableFuture =
                        moneyTable.setMoney(chatter.getName(), pocketAmount - amount);

                CompletableFuture<Boolean> completableFuture1 =
                        bankTable.setBankMoney(chatter.getName(), currentBankBalance + amount);

                try {

                    if(completableFuture.get() && completableFuture1.get()) {

                        Main.util.sendMessage(chatter, MessagePath.BankDepositSuccessfully.getPath()
                                .replace("#amount#", String.valueOf(amount))
                                .replace("#currency#", Main.util.getCurrency(amount)));
                        sounds.soundOnSuccess(chatter);
                        plugin.bankDepositValue.remove(chatter);
                        return;
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            if (amount > pocketAmount) {
                Main.util.sendMessage(chatter, MessagePath.BankDepositNotEnough.getPath());
                sounds.soundOnFailure(chatter);

            }


        } catch (NumberFormatException exception) {
            Main.util.sendMessage(chatter, MessagePath.NotANumber.getPath());
            //plugin.bankDepositValue.remove(chatter);
            throw new RuntimeException("Not a Number Exception: Please send this error to lightPlugins", exception);
        }
    }
}
