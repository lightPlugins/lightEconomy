package de.lightplugins.economy.events;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class NewPlayer implements Listener {

    public Main plugin;
    public NewPlayer(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        /*

            TODO: Rename feature before create Player !!!

         */

        Player player = event.getPlayer();

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(plugin);
        BankTableAsync bankTableAsync = new BankTableAsync(plugin);
        moneyTableAsync.playerBalance(player.getName()).thenAccept(balance -> {

            if(balance != null) {
                Main.debugPrinting.sendInfo("User already existing in Database. Checking for Name update ... ");

                moneyTableAsync.updatePlayerName(player.getName()).thenAccept(result -> {
                    // silence Playername update every time, if the player connect to the server
                });

            } else {
                moneyTableAsync.createNewPlayer(player.getName()).thenAccept(success -> {
                    if(success) {

                        Main.debugPrinting.sendInfo("New Player was putting in database!");

                        FileConfiguration settings = Main.settings.getConfig();

                        if(!settings.getBoolean("settings.enable-first-join-message")) {
                            return;
                        }

                        FileConfiguration messages = Main.messages.getConfig();

                        double startBalance = settings.getDouble("settings.start-balance");

                        Main.util.sendMessage(player, Objects.requireNonNull(messages.getString("firstJoinMessage"))
                                .replace("#startbalance#", String.valueOf(startBalance))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    }
                });
            }
        });

        CompletableFuture<Boolean> completableFuture = bankTableAsync.createBankAccount(player.getName());

        try {
            if(completableFuture.get()) {
                Main.debugPrinting.sendInfo("Successfully created bank account!");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @EventHandler
    public void test(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.IRON_PICKAXE) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.DIAMOND_ORE) {
                // Verlängere die Abbauzeit des Steins
                event.setCancelled(true); // Blockabbau verhindern
                Bukkit.getLogger().log(Level.INFO, "TEST: " + event.getClickedBlock().getBlockPower());
                player.sendBlockDamage(clickedBlock.getLocation(), 0.1f); // Sende den Schaden an den Block
            }
        }

        event.getPlayer().sendBlockDamage(event.getPlayer().getLocation(), 0.1f);
    }
}
