package de.lightplugins.economy.listener;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.items.Voucher;
import de.lightplugins.economy.master.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class LoseMoney implements Listener {


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        FileConfiguration lose = Main.lose.getConfig();


        if(!lose.getBoolean("lose.enable")) {
            return;
        }

        Player player = event.getEntity().getPlayer();

        if(player == null) {
            return;
        }

        String currentWorldName = player.getWorld().getName();

        for(String worldsetting :
                Objects.requireNonNull(lose.getConfigurationSection("lose.worlds")).getKeys(false)) {

            double minPocketBalance = lose.getDouble("lose.worlds." + worldsetting + ".min-amount-for-trigger");
            double triggerChance = lose.getDouble("lose.worlds." + worldsetting + ".trigger-chance");
            double losePercentage = lose.getDouble("lose.worlds." + worldsetting + ".lose-percentage");
            String immunityPerm = lose.getString("lose.worlds." + worldsetting + ".immunity-permission")
                    .replace("#worldname#", worldsetting);
            boolean voucherDrop = lose.getBoolean("lose.worlds." + worldsetting + ".drop-money-as-voucher");

            if(currentWorldName.equalsIgnoreCase(worldsetting)) {

                if(player.hasPermission(immunityPerm)) {
                    return;
                }

                MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

                try {

                    double currentPocket = moneyTableAsync.playerBalance(player.getName()).get();

                    if(currentPocket < minPocketBalance) {
                        return;
                    }

                    if(currentPocket == 0.00) {
                        return;
                    }

                    if(!Main.util.checkPercentage(triggerChance)) {
                        return;
                    }

                    double loseAmount = Main.util.subtractPercentage(currentPocket, losePercentage);

                    EconomyResponse ecoWithdraw = Main.economyImplementer.withdrawPlayer(player.getName(), loseAmount);

                    if(ecoWithdraw.transactionSuccess()) {
                        Main.util.sendMessage(player, MessagePath.LoseMoneyOnDeath.getPath()
                                .replace("#amount#", Main.util.formatDouble(loseAmount))
                                .replace("#currency#", Main.util.getCurrency(loseAmount)));
                    }

                    if(voucherDrop) {

                        Voucher voucherCreator = new Voucher();

                        ItemStack voucher = voucherCreator.createVoucher(loseAmount, player.getName());

                        Item droppedVoucher = player.getWorld().dropItem(player.getLocation(), voucher);
                        droppedVoucher.setCustomNameVisible(true);
                        return;

                    }


                }catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException("Something went wrong on player add money", e);
                }

                return;
            }
        }
    }
}
