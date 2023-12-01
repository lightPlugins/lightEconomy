package de.lightplugins.economy.inventories;


import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.master.Main;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class BankManageMenu implements InventoryProvider {

    private static final FileConfiguration bankManager = null;

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("BANK_MANAGE_MENU")
            .provider(new BankManageMenu())
            .size(bankManager.getInt("manage.main.size"),9)
            .title(Main.colorTranslation.hexTranslation(bankManager.getString("manage.main.title")))
            .manager(Main.bankMenuInventoryManager)
            .build();


    @Override
    public void init(Player player, InventoryContents inventoryContents) {

        Pagination pagination = inventoryContents.pagination();
        int state = inventoryContents.property("state", 0);
        inventoryContents.setProperty("state", state + 1);

        BankTableAsync bankTable = new BankTableAsync(Main.getInstance);
        MoneyTableAsync moneyTable = new MoneyTableAsync(Main.getInstance);

        CompletableFuture<Integer> levelFuture = bankTable.playerCurrentBankLevel(player.getName());

        int level = 0;
        try {
            level = levelFuture.get();
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

        CompletableFuture<List<String>> trustedFuture = null; // TODO: !!!!!!
        List<String> trustedList;
        try {
            trustedList = trustedFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int trustedListSize = 5; //trustedList.size();

        ClickableItem[] trustedPlayerClickable = new ClickableItem[trustedListSize];


        if(state % 5 != 0) { return; }

        //short durability = (short) random.nextInt(15);
        ItemStack glass  = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        assert glassMeta != null;
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        inventoryContents.fill(ClickableItem.empty(glass));


        int i = 0;

        //  Check if the list is not empty

        if(trustedListSize >= 1) {

            for(String singlePlayer : trustedList) {
                i++;

                String[] splittedData = singlePlayer.split("#");

                String uuid = splittedData[0];
                UUID uuidTest = UUID.fromString(uuid);
                double amount = Double.parseDouble(splittedData[1]);
                Date date = new Date(Long.parseLong(splittedData[2]));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String formattedDate = dateFormat.format(date);

                OfflinePlayer offlinePlayer = Bukkit.getPlayer(uuidTest);
                ItemStack playerSkull = new ItemStack(Material.BARRIER, 1);

                if(offlinePlayer == null) {
                    playerSkull.setType(Material.BEDROCK);
                }
                Bukkit.getLogger().log(Level.SEVERE, "ERROR " + uuid);

                playerSkull = createSkullItem(offlinePlayer);
                ItemMeta playerSkullMeta = playerSkull.getItemMeta();

                if(playerSkullMeta == null) {

                    return;
                }

                String displayName = bankManager.getString("manage.main.single-user.displayname");

                if(displayName == null) {
                    displayName = "&4Config Error in bank-manage.yml";
                }

                String finalDisplayName = displayName
                        .replace("#player#", (offlinePlayer == null) ? "TEST1" : offlinePlayer.getName())
                        .replace("#date#", formattedDate)
                        .replace("#amoun#", String.valueOf(amount));

                playerSkullMeta.setDisplayName(Main.colorTranslation.hexTranslation(finalDisplayName));

                if(playerSkullMeta.hasLore()) { Objects.requireNonNull(playerSkullMeta.getLore()).clear();}
                List<String> lore = new ArrayList<>();

                for(String line : bankManager.getStringList("manage.main.single-user.lore")) {

                    String finalLine = line
                            .replace("#player#", (offlinePlayer == null) ? "TEST1" : offlinePlayer.getName())
                            .replace("#date#", formattedDate)
                            .replace("#amoun#", String.valueOf(amount));

                    lore.add(Main.colorTranslation.hexTranslation(finalLine));
                }

                playerSkullMeta.setLore(lore);
                playerSkull.setItemMeta(playerSkullMeta);

                trustedPlayerClickable[i - 1] = ClickableItem.of(playerSkull, e -> {

                    Main.util.sendMessage(player, "Das ist player " + singlePlayer);
                    return;

                });
            }
        } else {

            ItemStack noTrustedUserFound = new ItemStack(Material.BARRIER, 1);
            inventoryContents.set(1, 1, ClickableItem.of(noTrustedUserFound,
                    e -> {  }));

        }


        pagination.setItems(trustedPlayerClickable);
        pagination.setItemsPerPage(14);
        pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1,1)
                .blacklist(1, 8).blacklist(2, 0));

        //Previous Page ItemStack

        ItemStack previousPageItemStack = getItemStackFromConfig("previous-page");

        int rowPrevious = bankManager.getInt("manage.main.content.previous-page.row");
        int columnPrevious = bankManager.getInt("manage.main.content.previous-page.column");


        //Next Page ItemStack

        ItemStack nextPageItemStack = getItemStackFromConfig("next-page");

        int rowNext = bankManager.getInt("manage.main.content.next-page.row");
        int columnNext = bankManager.getInt("manage.main.content.next-page.column");


        // back button

        ItemStack backItemStack = getItemStackFromConfig("back-button");

        int rowBack = bankManager.getInt("manage.main.content.back-button.row");
        int columnBack = bankManager.getInt("manage.main.content.back-button.column");


        inventoryContents.set(rowPrevious, columnPrevious, ClickableItem.of(previousPageItemStack,
                e -> INVENTORY.open(player, pagination.previous().getPage())));
        inventoryContents.set(rowNext, columnNext, ClickableItem.of(nextPageItemStack,
                e -> INVENTORY.open(player, pagination.next().getPage())));
        inventoryContents.set(rowBack, columnBack, ClickableItem.of(backItemStack,
                e -> BankMainMenu.INVENTORY.open(player)));
        
        
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

    private ItemStack createSkullItem(OfflinePlayer player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if(meta == null) {
            return new ItemStack(Material.STONE, 1);
        }
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getItemStackFromConfig(String path) {

        Material backMaterial = Material.valueOf(bankManager.getString("manage.main.content." + path + ".material"));
        String backDisplayName= bankManager.getString("manage.main.content." + path + ".displayname");

        ItemStack itemStack = new ItemStack(backMaterial);
        ItemMeta itemStackMeta = itemStack.getItemMeta();

        if(itemStackMeta == null) {
            return new ItemStack(Material.STONE, 1);
        }

        if(itemStackMeta.hasLore()) { Objects.requireNonNull(itemStackMeta.getLore()).clear();}
        List<String> lore = new ArrayList<>();
        for(String line : bankManager.getStringList("manage.main.content." + path + ".lore")) {

            lore.add(Main.colorTranslation.hexTranslation(line));
        }

        itemStackMeta.setLore(lore);

        itemStackMeta.setDisplayName(Main.colorTranslation.hexTranslation(backDisplayName));
        itemStack.setItemMeta(itemStackMeta);

        return itemStack;

    }
}
