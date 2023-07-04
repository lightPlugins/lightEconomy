package de.lightplugins.economy.inventories;


import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.BankLevelSystem;
import de.lightplugins.economy.utils.Sounds;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class BankMainMenu implements InventoryProvider {

    public static final FileConfiguration bankMenu = Main.bankMenu.getConfig();

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("BANK_MAIN_MENU")
            .provider(new BankMainMenu())
            .size(bankMenu.getInt("bank.main.size"),9)
            .title(Main.colorTranslation.hexTranslation(bankMenu.getString("bank.main.title")))
            .manager(Main.bankMenuInventoryManager)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {

        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);

        BankLevelSystem bankLevelSystem = new BankLevelSystem(Main.getInstance);
        BankTableAsync bankTable = new BankTableAsync(Main.getInstance);
        MoneyTableAsync moneyTable = new MoneyTableAsync(Main.getInstance);

        CompletableFuture<Integer> levelFuture = bankTable.playerCurrentBankLevel(player.getName());

        int level = 0;
        try {
            level = levelFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double limitFuture = bankLevelSystem.getLimitByLevel(player.getUniqueId());


        double limit = 0;
        try {
            limit = limitFuture;
        } catch (Exception e) {
            e.printStackTrace();
        }

        CompletableFuture<Double> bankBalanceFuture = bankTable.playerBankBalance(player.getName());

        double bankBalance = 0;
        try {
            bankBalance = bankBalanceFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CompletableFuture<Double> pocketBalanceFuture = moneyTable.playerBalance(player.getName());

        double pocketBalance = 0;
        try {
            pocketBalance = pocketBalanceFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(state % 5 != 0) { return; }

        //short durability = (short) random.nextInt(15);
        ItemStack glass  = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        assert glassMeta != null;
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        contents.fill(ClickableItem.empty(glass));

        for(String input :
                Objects.requireNonNull(bankMenu.getConfigurationSection("bank.main.content")).getKeys(false)) {

            if(input.equalsIgnoreCase("fillItem")) { return; }

            int column = Integer.parseInt(
                    Objects.requireNonNull(bankMenu.getString("bank.main.content." + input + ".column")));
            int row = Integer.parseInt(
                    Objects.requireNonNull(bankMenu.getString("bank.main.content." + input + ".row")));
            Material material = Material.valueOf(
                    bankMenu.getString("bank.main.content." + input + ".material"));
            String displayName = bankMenu.getString("bank.main.content." + input + ".displayname");


            List<String> lore = new ArrayList<>();

            int[] maxLevel = {0};

            Main.bankLevelMenu.getConfig().getStringList("levels").forEach(e -> {
                maxLevel[0] ++;
            });

            for(String line : bankMenu.getStringList("bank.main.content." + input + ".lore")) {

                String finalLine = line
                        .replace("%bank_owner%", player.getName())
                        .replace("%bank_balance%", Main.util.finalFormatDouble(bankBalance))
                        .replace("%bank_level%", String.valueOf(level))
                        .replace("%bank_level_max%", String.valueOf(maxLevel[0]))
                        .replace("%level_based_max_value%", Main.util.finalFormatDouble(limit))
                        .replace("%pocket_balance%", Main.util.finalFormatDouble(pocketBalance));

                lore.add(Main.colorTranslation.hexTranslation(finalLine));
            }

            ItemStack itemStack = new ItemStack(material, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();

            assert itemMeta != null;
            itemMeta.setDisplayName(Main.colorTranslation.hexTranslation(displayName));

            if(itemMeta.hasLore()) {
                Objects.requireNonNull(itemMeta.getLore()).clear();
            }


            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            double finalBankBalance = bankBalance;
            double finalLimit = limit;
            double finalPocketBalance = pocketBalance;

            /*
                Bank System Listener - clickable items
             */

            contents.set(row,column,ClickableItem.of(itemStack, e -> {

                Sounds sounds = new Sounds();

                if(input.equalsIgnoreCase("deposit-value")) {
                    if(!Main.getInstance.bankDepositValue.contains(player)) {

                        Main.getInstance.bankDepositValue.add(player);
                        Main.util.sendMessage(player, MessagePath.BankDepositValueViaChat.getPath());
                        player.closeInventory();
                        sounds.soundOnSuccess(player);
                        return;
                    }
                }
                if(input.equalsIgnoreCase("deposit-all")) {

                    double currentPocketBalance;

                    try {

                    CompletableFuture<Double> currentPlayerBalance = moneyTable.playerBalance(player.getName());

                        currentPocketBalance = currentPlayerBalance.get();

                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }

                    if(currentPocketBalance == 0.0) {
                        Main.util.sendMessage(player, MessagePath.BankDepositNotEnough.getPath());
                        player.closeInventory();
                        sounds.soundOnFailure(player);
                        return;
                    }

                    if(finalBankBalance == finalLimit) {
                        Main.util.sendMessage(player, MessagePath.BankDepositNotPossible.getPath());
                        sounds.soundOnFailure(player);
                        player.closeInventory();
                        return;

                    }


                    if(currentPocketBalance >= finalLimit) {

                        CompletableFuture<Boolean> completableFuture = bankTable.setBankMoney(player.getName(), finalLimit);
                        CompletableFuture<Boolean> completableFuture1 = moneyTable.setMoney(player.getName(), (currentPocketBalance - finalLimit));


                        try {

                            if(completableFuture1.get() && completableFuture.get()) {
                                Main.util.sendMessage(player, MessagePath.BankDepositAllLimit.getPath()
                                        .replace("#amount#", Main.util.finalFormatDouble(finalLimit - finalBankBalance))
                                        .replace("#currency#", Main.economyImplementer.currencyNamePlural()));
                                sounds.soundOnSuccess(player);
                                player.closeInventory();
                                return;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        player.closeInventory();
                        return;
                    }

                    CompletableFuture<Boolean> completableFuture1 =
                            bankTable.setBankMoney(player.getName(), finalBankBalance + currentPocketBalance);

                    CompletableFuture<Boolean> completableFuture =
                            moneyTable.setMoney(player.getName(), 0.0);

                    try {

                        if(completableFuture.get() && completableFuture1.get()) {
                            Main.util.sendMessage(player, MessagePath.BankDepositAll.getPath()
                                    .replace("#amount#", String.valueOf(currentPocketBalance))
                                    .replace("#currency#", Main.economyImplementer.currencyNamePlural()));
                            sounds.soundOnSuccess(player);
                            player.closeInventory();
                            return;
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if(input.equalsIgnoreCase("withdraw-value")) {
                    if(!Main.getInstance.bankWithdrawValue.contains(player)) {

                        Main.getInstance.bankWithdrawValue.add(player);

                        Main.util.sendMessage(player, MessagePath.BankWithdrawValueViaChat.getPath());

                        player.closeInventory();
                        sounds.soundOnSuccess(player);
                        return;
                    }
                }
                if(input.equalsIgnoreCase("withdraw-all")) {

                    CompletableFuture<Boolean> completableFuture =
                            moneyTable.setMoney(player.getName(), finalPocketBalance + finalBankBalance);

                    CompletableFuture<Boolean> completableFuture1 =
                            bankTable.setBankMoney(player.getName(), 0.0);

                    try {

                        if(completableFuture.get()  && completableFuture1.get()) {

                            Main.util.sendMessage(player, MessagePath.BankWithdrawAll.getPath());
                            sounds.soundOnSuccess(player);
                            player.closeInventory();
                            return;

                        }

                        Material.DEEP

                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                if(input.equalsIgnoreCase("latest-transactions")) {

                }
                if(input.equalsIgnoreCase("bank-informations")) {
                    BankLevelMenu.INVENTORY.open(player);

                }
                if(input.equalsIgnoreCase("friends")) {

                }
                if(input.equalsIgnoreCase("back-button")) {
                    player.closeInventory();
                }

            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
