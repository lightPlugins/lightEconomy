package de.lightplugins.economy.listener;

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
import org.bukkit.inventory.ItemStack;

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

        for(String worldsetting : lose.getConfigurationSection("lose.worlds").getKeys(false)) {

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

                double currentPocket = Main.economyImplementer.getBalance(player.getName());

                if(currentPocket < minPocketBalance) {
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
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                }

                if(voucherDrop) {

                    Voucher voucherCreator = new Voucher();

                    ItemStack voucher = voucherCreator.createVoucher(loseAmount, player.getName());

                    Item droppedVoucher = player.getWorld().dropItem(player.getLocation(), voucher);
                    droppedVoucher.setCustomNameVisible(true);
                    return;

                }

                return;
            }
        }
    }
}
