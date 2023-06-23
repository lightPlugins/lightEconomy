package de.lightplugins.economy.inventories;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.Sounds;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BankLevelMenu implements InventoryProvider {

    private static final FileConfiguration fileConfiguration = Main.bankLevelMenu.getConfig();

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("BANK_LEVEL_MENU")
            .provider(new BankLevelMenu())
            .size(3,9)
            .title(Main.colorTranslation.hexTranslation(fileConfiguration.getString("gui.title")))
            .manager(Main.bankMenuInventoryManager)
            .build();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {

        Pagination pagination = inventoryContents.pagination();
        FileConfiguration config = Main.bankLevelMenu.getConfig();
        Sounds sounds = new Sounds();
        int levelCounter =
                Objects.requireNonNull(config.getConfigurationSection("levels")).getKeys(false).size();

        BankTableAsync bankTable = new BankTableAsync(Main.getInstance);
        //CompletableFuture<Integer> currentBankLevelFuture = bankTable.playerCurrentBankLevel(player.getName());

        MoneyTableAsync moneyTable = new MoneyTableAsync(Main.getInstance);

        ClickableItem[] levelItems = new ClickableItem[levelCounter];

        //Fill all empty spaces with glass
        ItemStack glass  = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        assert glassMeta != null;
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        inventoryContents.fill(ClickableItem.empty(glass));

        int i = 0;
        for(String path : Objects.requireNonNull(config.getConfigurationSection("levels")).getKeys(false)) {
            i++;

            Material material = Material.valueOf(config.getString("levels." + path + ".material"));
            int level = config.getInt("levels." + path + ".level");

            String permission = config.getString("levels." + path + ".needed-permission");
            assert permission != null;

            String title = config.getString("levels." + path + ".title");
            assert title != null;

            double price = config.getDouble("levels." + path + ".price");
            double limit = config.getDouble("levels." + path + ".max-value");


            ItemStack is = new ItemStack(material);
            is.setAmount(i);

            ItemMeta im = is.getItemMeta();
            assert im != null;
            im.setDisplayName(Main.colorTranslation.hexTranslation(title
                    .replace("#level#", String.valueOf(level))));

            //setup lore System
            List<String> lore = new ArrayList<>();
            for(String list : config.getStringList("levels." + path + ".lore")) {

                String finalLore = list
                        .replace("#level#", String.valueOf(level))
                        .replace("#permission#", permission)
                        .replace("#amount#", String.valueOf(price))
                        .replace("#limit#", String.valueOf(limit))
                        .replace("#currency#", Main.economyImplementer.currencyNamePlural());

                lore.add(Main.colorTranslation.hexTranslation(finalLore));
            }

            im.setLore(lore);
            is.setItemMeta(im);

            int levelValue = i;
            int currentBankLevel;
            CompletableFuture<Integer> completableFuture = bankTable.playerCurrentBankLevel(player.getName());

            try {
                currentBankLevel = completableFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!player.hasPermission(permission)) {
                if(currentBankLevel < levelValue) {

                    //Wenn Spieler keine Berechtigung haben ...

                    Material noPermMaterial = Material.valueOf(config.getString("gui.other.no-permission.material"));
                    is.setType(noPermMaterial);

                    List<String> noPermExtraLore = im.getLore();
                    assert noPermExtraLore != null;
                    noPermExtraLore.add(Main.colorTranslation.hexTranslation(
                            config.getString("gui.other.no-permission.lore-extra")));

                    im.setLore(noPermExtraLore);

                    if(config.getBoolean("gui.other.no-permission.glow")) {

                        im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    }

                    is.setItemMeta(im);

                }
            }


            if(currentBankLevel >= levelValue) {

                //Wenn Spieler schon das Level hat ...

                Material alreadyBoughtMaterial = Material.valueOf(config.getString("gui.other.already-bought.material"));
                is.setType(alreadyBoughtMaterial);

                List<String> alreadyBoughtExtraLore = im.getLore();
                assert alreadyBoughtExtraLore != null;
                alreadyBoughtExtraLore.add(Main.colorTranslation.hexTranslation(
                        config.getString("gui.other.already-bought.lore-extra")));

                im.setLore(alreadyBoughtExtraLore);

                if(config.getBoolean("gui.other.already-bought.glow")) {

                    im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }

                is.setItemMeta(im);
            }


            levelItems[i - 1] = ClickableItem.of(is, e -> {

                //TODO Actions if left Click on the Item ....

                if(!player.hasPermission(permission) && currentBankLevel < levelValue) {

                    //Player had no Permission

                    Main.util.sendMessage(player, MessagePath.BankUpgradeNoPermission.getPath()
                            .replace("#permission#", permission));
                    //player.closeInventory();
                    sounds.soundOnFailure(player);
                    return;

                }

                if(currentBankLevel >= levelValue) {

                    //Player had this level already

                    Main.util.sendMessage(player, MessagePath.BankUpgradeAlreadyOwn.getPath());
                    //player.closeInventory();
                    sounds.soundOnFailure(player);
                    return;

                }

                if(levelValue > (currentBankLevel + 1)) {

                    //Player need previous Level to buy
                    Main.util.sendMessage(player, MessagePath.BankUpgradeNeedPreviousLevel.getPath());
                    //player.closeInventory();
                    sounds.soundOnFailure(player);
                    return;
                }

                if(levelValue == (currentBankLevel + 1)) {

                    double currentPocketBalance;

                    CompletableFuture<Double> future = moneyTable.playerBalance(player.getName());

                    try {
                        currentPocketBalance = future.get();
                    } catch (ExecutionException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }


                    if(currentPocketBalance < price) {
                        Main.util.sendMessage(player, MessagePath.BankUpgradeNoMoney.getPath());
                        player.closeInventory();
                        sounds.soundOnFailure(player);
                        return;
                    }
                    CompletableFuture<Boolean> moneyFuture =
                            moneyTable.setMoney(player.getName(), currentPocketBalance - price);

                    CompletableFuture<Boolean> bankFuture =
                            bankTable.setBankLevel(player.getName(), level);

                    try {

                        if(moneyFuture.get() && bankFuture.get()) {
                            Main.util.sendMessage(player, MessagePath.BankUpgradeSuccess.getPath()
                                    .replace("#level#", String.valueOf(level)));
                            player.closeInventory();
                            sounds.soundOnSuccess(player);
                            return;

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


        }

        pagination.setItems(levelItems);
        pagination.setItemsPerPage(7);

        //Previous Page ItemStack

        pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1,1));

        Material previousPageMaterial = Material.valueOf(config.getString("gui.pages.previous-page.material"));
        String previousPageDisplayName= config.getString("gui.pages.previous-page.displayname");

        ItemStack previousPageItemStack = new ItemStack(previousPageMaterial);
        ItemMeta previousPageItemMeta = previousPageItemStack.getItemMeta();

        assert previousPageItemMeta != null;
        if(previousPageItemMeta.hasLore()) { Objects.requireNonNull(previousPageItemMeta.getLore()).clear();}

        previousPageItemMeta.setDisplayName(Main.colorTranslation.hexTranslation(previousPageDisplayName));

        previousPageItemStack.setItemMeta(previousPageItemMeta);



        //Next Page ItemStack

        Material nextPageMaterial = Material.valueOf(config.getString("gui.pages.next-page.material"));
        String nextPageDisplayName= config.getString("gui.pages.next-page.displayname");

        ItemStack nextPageItemStack = new ItemStack(nextPageMaterial);
        ItemMeta nextPageItemMeta = nextPageItemStack.getItemMeta();

        assert nextPageItemMeta != null;
        if(nextPageItemMeta.hasLore()) { Objects.requireNonNull(nextPageItemMeta.getLore()).clear();}

        nextPageItemMeta.setDisplayName(Main.colorTranslation.hexTranslation(nextPageDisplayName));

        nextPageItemStack.setItemMeta(nextPageItemMeta);


        inventoryContents.set(2, 3, ClickableItem.of(previousPageItemStack,
                e -> INVENTORY.open(player, pagination.previous().getPage())));
        inventoryContents.set(2, 5, ClickableItem.of(nextPageItemStack,
                e -> INVENTORY.open(player, pagination.next().getPage())));





    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
