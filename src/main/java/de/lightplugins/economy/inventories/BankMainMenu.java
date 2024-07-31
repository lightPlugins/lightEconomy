package de.lightplugins.economy.inventories;


import com.google.common.collect.Lists;
import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.implementer.EconomyImplementer;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.BankLevelSystem;
import de.lightplugins.economy.utils.SignPackets;
import de.lightplugins.economy.utils.Sounds;
import de.lightplugins.economy.utils.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class BankMainMenu implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("BANK_MAIN_MENU")
            .provider(new BankMainMenu())
            .size(Main.bankMenu.getConfig().getInt("bank.main.size"),9)
            .title(Main.colorTranslation.hexTranslation(Main.bankMenu.getConfig().getString("bank.main.title")))
            .manager(Main.bankMenuInventoryManager)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {

        FileConfiguration bankMenu = Main.bankMenu.getConfig();

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
            throw new RuntimeException(e);
        }

        double limitFuture = bankLevelSystem.getLimitByLevel(player.getUniqueId());


        double limit = 0;
        try {
            limit = limitFuture;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CompletableFuture<Double> bankBalanceFuture = bankTable.playerBankBalance(player.getName());

        double bankBalance = 0;
        try {
            bankBalance = bankBalanceFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CompletableFuture<Double> pocketBalanceFuture = moneyTable.playerBalance(player.getName());

        double pocketBalance = 0;
        try {
            pocketBalance = pocketBalanceFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        if(state % 5 != 0) { return; }

        ItemStack fillItem = new ItemStack(Material.valueOf(
                bankMenu.getString("bank.main.content.fillItem.material")), 1);

        int customModelData = bankMenu.getInt("bank.main.content.fillItem.customModelData");

        ItemMeta fillItemMeta = fillItem.getItemMeta();

        if(fillItemMeta == null) {
            fillItem.setType(Material.STONE);
            return;
        }

        if(customModelData != 0) {
            fillItemMeta.setCustomModelData(customModelData);
        }

        fillItemMeta.setDisplayName(bankMenu.getString("bank.main.content.fillItem.displayname"));

        if(fillItemMeta.getLore() != null) {
            fillItemMeta.getLore().clear();

        }

        fillItem.setItemMeta(fillItemMeta);

        contents.fill(ClickableItem.empty(fillItem));

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

            FileConfiguration config = Main.bankLevelMenu.getConfig();

            int maxLevelViaConfig = 0;

            for(int i = 0; i < config.getConfigurationSection("levels").getKeys(false).size(); i++) {
                maxLevelViaConfig ++;
            }

            for(String line : bankMenu.getStringList("bank.main.content." + input + ".lore")) {

                String finalLine = line
                        .replace("%bank_owner%", player.getName())
                        .replace("%bank_balance%", Main.util.finalFormatDouble(bankBalance))
                        .replace("%bank_level%", String.valueOf(level))
                        .replace("%bank_level_max%", String.valueOf(maxLevelViaConfig))
                        .replace("%level_based_max_value%", Main.util.finalFormatDouble(limit))
                        .replace("%pocket_balance%", Main.util.finalFormatDouble(pocketBalance))
                        .replace("%currencyBank%", Main.util.getCurrency(bankBalance))
                        .replace("%currencyPocket%", Main.util.getCurrency(pocketBalance));

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

                FileConfiguration settings = Main.settings.getConfig();
                Sounds sounds = new Sounds();

                String line2 = Main.colorTranslation.hexTranslation(
                        settings.getString("settings.bankInputViaSign.bankSignLine2"));
                String line3 = Main.colorTranslation.hexTranslation(
                        settings.getString("settings.bankInputViaSign.bankSignLine3"));
                String line4 = Main.colorTranslation.hexTranslation(
                        settings.getString("settings.bankInputViaSign.bankSignLine4"));


                if(input.contains("deposit-value")) {

                    if(!settings.getBoolean("settings.bankInputViaSign.enable")) {
                        if(!Main.getInstance.bankDepositValue.contains(player)) {

                            Main.getInstance.bankDepositValue.add(player);
                            Main.util.sendMessage(player, MessagePath.BankDepositValueViaChat.getPath());
                            player.closeInventory();
                            sounds.soundOnSuccess(player);
                            return;
                        }
                    }

                    SignPackets.Menu menu = Main.getInstance.signGui
                            .newMenu(Arrays.asList("", line2, line3, line4))
                            .reopenIfFail(true)
                            .response((target, lines) -> {

                                if(Main.util.isNumber(lines[0])) {
                                    double signInput = Double.parseDouble(lines[0]);
                                    double currentBankBalance = 0;
                                    double currentPocketBalance = 0;

                                    BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);

                                    CompletableFuture<Double> currentBank =
                                            bankTableAsync.playerBankBalance(target.getName());
                                    CompletableFuture<Double> currentPocket =
                                            moneyTable.playerBalance(target.getName());

                                    try {
                                        currentBankBalance = currentBank.get();
                                        currentPocketBalance = currentPocket.get();
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }

                                    if(signInput < 0) {
                                        Main.util.sendMessage(target, MessagePath.OnlyPositivNumbers.getPath());
                                        sounds.soundOnFailure(target);
                                        return true;
                                    }

                                    if(signInput == 0) {
                                        return true;
                                    }

                                    if(signInput > currentPocketBalance) {
                                        Main.util.sendMessage(target, MessagePath.BankWithdrawNotEnough.getPath());
                                        sounds.soundOnFailure(target);
                                        return true;
                                    }

                                    if(currentBankBalance == bankLevelSystem.getLimitByLevel(player.getUniqueId())) {
                                        Main.util.sendMessage(player, MessagePath.BankDepositNotPossible.getPath());
                                        sounds.soundOnFailure(player);
                                        Main.getInstance.bankDepositValue.remove(player);
                                        return true;

                                    }

                                    if(signInput > bankLevelSystem.getLimitByLevel(player.getUniqueId())) {
                                        Main.util.sendMessage(player, MessagePath.BankDepositOverLimit.getPath());
                                        sounds.soundOnFailure(player);
                                        Main.getInstance.bankDepositValue.remove(player);
                                        return true;
                                    }

                                    if(currentBankBalance + signInput > bankLevelSystem.getLimitByLevel(player.getUniqueId())) {
                                        Main.util.sendMessage(player, MessagePath.BankDepositOverLimit.getPath());
                                        sounds.soundOnFailure(player);
                                        Main.getInstance.bankDepositValue.remove(player);
                                        return true;
                                    }

                                    if(signInput <= currentPocketBalance) {

                                        try {

                                            CompletableFuture<Boolean> completableFuture1 =
                                                    bankTable.setBankMoney(target.getName(),
                                                            currentBankBalance + signInput);

                                            EconomyResponse economyResponse = Main.economyImplementer.
                                                    withdrawPlayer(target, signInput);

                                            if(economyResponse.transactionSuccess() && completableFuture1.get()) {

                                                Main.util.sendMessage(target, MessagePath.BankDepositSuccessfully.getPath()
                                                        .replace("#amount#", Main.util.finalFormatDouble(signInput))
                                                        .replace("#currency#", Main.util.getCurrency(signInput)));
                                                sounds.soundOnSuccess(target);
                                                return true;


                                            }

                                        } catch (Exception ex) {
                                            throw new RuntimeException("BankMainMenu", ex);
                                        }
                                    }
                                }

                                return true;
                            });

                    menu.open(player);

                }
                if(input.contains("deposit-all")) {

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


                    if(currentPocketBalance >= finalLimit || currentPocketBalance >= (finalLimit - finalBankBalance)) {

                        CompletableFuture<Boolean> completableFuture =
                                bankTable.setBankMoney(player.getName(), finalLimit);
                        CompletableFuture<Boolean> completableFuture1 =
                                moneyTable.setMoney(player.getName(),
                                        (currentPocketBalance - (finalLimit - finalBankBalance)));


                        try {

                            if(completableFuture1.get() && completableFuture.get()) {
                                Main.util.sendMessage(player, MessagePath.BankDepositAllLimit.getPath()
                                        .replace("#amount#",
                                                Main.util.finalFormatDouble(finalLimit - finalBankBalance))
                                        .replace("#currency#",
                                                Main.util.getCurrency(finalLimit - finalBankBalance)));
                                sounds.soundOnSuccess(player);
                                player.closeInventory();
                                return;
                            }

                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
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
                                    .replace("#amount#", Main.util.finalFormatDouble(currentPocketBalance))
                                    .replace("#currency#", Main.util.getCurrency(currentPocketBalance)));
                            sounds.soundOnSuccess(player);
                            player.closeInventory();
                            return;
                        }

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }



                if(input.contains("withdraw-value")) {

                    //player.closeInventory();

                    Bukkit.getLogger().log(Level.WARNING, "TEST 1 + " + line2 + line3 + line4);

                    player.closeInventory();

                    if(!settings.getBoolean("settings.bankInputViaSign.enable")) {
                        if(!Main.getInstance.bankWithdrawValue.contains(player)) {

                            Main.getInstance.bankWithdrawValue.add(player);

                            Main.util.sendMessage(player, MessagePath.BankWithdrawValueViaChat.getPath());

                            player.closeInventory();
                            sounds.soundOnSuccess(player);
                            return;

                        }
                    }


                    SignPackets.Menu menu = Main.getInstance.signGui
                            .newMenu(Arrays.asList("", line2, line3, line4))
                            .reopenIfFail(true)
                            .response((target, lines) -> {

                                if(Main.util.isNumber(lines[0])) {
                                    double signInput = Double.parseDouble(lines[0]);
                                    double currentBankBalance = 0;

                                    BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);

                                    CompletableFuture<Double> currentBank =
                                            bankTableAsync.playerBankBalance(target.getName());

                                    try {
                                        currentBankBalance = currentBank.get();
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }

                                    if(signInput < 0) {
                                        Main.util.sendMessage(target, MessagePath.OnlyPositivNumbers.getPath());
                                        sounds.soundOnFailure(target);
                                        return true;
                                    }

                                    if(signInput == 0) {
                                        return true;
                                    }

                                    if(signInput > currentBankBalance) {
                                        Main.util.sendMessage(target, MessagePath.BankWithdrawNotEnough.getPath());
                                        sounds.soundOnFailure(target);
                                        return true;
                                    }

                                    if(signInput <= currentBankBalance) {

                                        try {

                                            CompletableFuture<Boolean> completableFuture1 =
                                                    bankTable.setBankMoney(target.getName(),
                                                            currentBankBalance - signInput);

                                            EconomyResponse economyResponse = Main.economyImplementer.
                                                    depositPlayer(target, signInput);

                                            if(economyResponse.transactionSuccess() && completableFuture1.get()) {

                                                Main.util.sendMessage(target, MessagePath.BankWithdrawSuccessfully.getPath()
                                                        .replace("#amount#", Main.util.finalFormatDouble(signInput))
                                                        .replace("#currency#", Main.util.getCurrency(signInput)));
                                                sounds.soundOnSuccess(target);
                                                return true;


                                            }

                                        } catch (Exception ex) {
                                            throw new RuntimeException("BankMainMenu", ex);
                                        }
                                    }
                                }

                                return true;
                            });

                            menu.open(player);
                }

                if(input.contains("withdraw-all")) {

                    CompletableFuture<Boolean> completableFuture =
                            moneyTable.setMoney(player.getName(), finalPocketBalance + finalBankBalance);

                    CompletableFuture<Boolean> completableFuture1 =
                            bankTable.setBankMoney(player.getName(), 0.0);

                    try {

                        if(completableFuture.get()  && completableFuture1.get()) {

                            Main.util.sendMessage(player, MessagePath.BankWithdrawAll.getPath()
                                    .replace("#amount#", Main.util.finalFormatDouble(finalBankBalance))
                                    .replace("#currency#", Main.util.getCurrency(finalBankBalance)));
                            sounds.soundOnSuccess(player);
                            player.closeInventory();
                            return;

                        }

                    }catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                }
                if(input.equalsIgnoreCase("manage-account")) {
                    //BankManageMenu.INVENTORY.open(player);

                }
                if(input.contains("bank-informations")) {
                    BankLevelMenu.INVENTORY.open(player);

                }
                if(input.equalsIgnoreCase("friends")) {

                }
                if(input.contains("back-button")) {
                    player.closeInventory();
                }

            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
