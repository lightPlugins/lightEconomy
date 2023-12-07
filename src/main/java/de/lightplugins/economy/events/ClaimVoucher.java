package de.lightplugins.economy.events;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PersistentDataPaths;
import de.lightplugins.economy.master.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;
import java.util.logging.Level;

public class ClaimVoucher implements Listener {

    @EventHandler
    public void onRightClickItem(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            if(itemStack != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();

                if(itemMeta == null) {
                    return;
                }

                if(event.getHand() == null) {
                    return;
                }

                if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                    return;
                }

                PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(Main.getInstance, PersistentDataPaths.MONEY_VALUE.getType());

                if(key.getKey().equalsIgnoreCase(PersistentDataPaths.MONEY_VALUE.getType())) {

                    if(!data.has(key, PersistentDataType.DOUBLE)) {
                        return;
                    }

                    event.setCancelled(true);

                    if(!Main.voucher.getConfig().getBoolean("voucher.enable")) {
                        Main.util.sendMessage(player, MessagePath.VoucherDisabled.getPath());
                        return;
                    }

                    double amount = data.get(key, PersistentDataType.DOUBLE);
                    EconomyResponse economyResponse = Main.economyImplementer.depositPlayer(player.getName(), amount);
                    if(economyResponse.transactionSuccess()) {
                        int currentInvSlot = event.getPlayer().getInventory().getHeldItemSlot();

                        Main.util.sendMessage(player, MessagePath.VoucherCollected.getPath()
                                .replace("#amount#", String.valueOf(amount))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular()));

                        event.setCancelled(true);

                        if(itemStack.getAmount() > 1) {
                            itemStack.setAmount(itemStack.getAmount() - 1);
                            player.getInventory().setItem(currentInvSlot, itemStack);
                            return;
                        }

                        player.getInventory().removeItem(itemStack);
                        return;
                    }

                    Main.util.sendMessage(player, MessagePath.TransactionFailed.getPath()
                            .replace("#reason#", economyResponse.errorMessage));
                }
            }
        }
    }

    @EventHandler
    public void onSecondHand(InventoryClickEvent event) {

        if(event.getCurrentItem() != null) {

            ItemStack itemStack = event.getCursor();
            assert itemStack != null;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) {
                return;
            }


            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance, PersistentDataPaths.MONEY_VALUE.getType());

            if(!data.has(key, PersistentDataType.DOUBLE)) {
                return;
            }

            if(event.getSlot() == 40) {
                event.setCancelled(true);
                Main.util.sendMessage((Player) event.getWhoClicked(), MessagePath.VoucherOffHanad.getPath());
                event.getWhoClicked().closeInventory();
            }
        }
    }

    @EventHandler
    public void offHandSwap(PlayerSwapHandItemsEvent event) {

        ItemStack itemStack = event.getOffHandItem();
        assert itemStack != null;
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return;
        }

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getInstance, PersistentDataPaths.MONEY_VALUE.getType());

        if(!data.has(key, PersistentDataType.DOUBLE)) {
            return;
        }

        event.setCancelled(true);
        Main.util.sendMessage(event.getPlayer(), MessagePath.VoucherOffHanad.getPath());

    }
}
