package de.lightplugins.lighteconomyv5.inventories;

import de.lightplugins.lighteconomyv5.master.Main;
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

public class MainMenu implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("MainMenu")
            .provider(new MainMenu())
            .size(6,9)
            .title("&cMainMenu")
            .build();
    @Override
    public void init(Player player, InventoryContents content) {
        content.fill(ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));

        FileConfiguration mainMenuConfig = Main.mainMenu.getConfig();

        for (String singleElement : mainMenuConfig.getStringList("inventory")) {

            int slot = mainMenuConfig.getInt(singleElement + ".slot");
            int row = mainMenuConfig.getInt(singleElement + ".row");
            String name = Main.colorTranslation.hexTranslation(mainMenuConfig.getString(singleElement + ".name"));
            Material material = Material.valueOf(mainMenuConfig.getString(singleElement + ".material"));
            List<String> lore = mainMenuConfig.getStringList(singleElement + ".lore");

            ItemStack is = new ItemStack(material, 1);
            ItemMeta im = is.getItemMeta();
            assert im != null;
            im.setDisplayName(name);


            String pocketProgressBar = Main.progressionBar.getProgressBar(
                    149.49, 500, 20, '|', "&c", "&7");

            List<String> finalLore = new ArrayList<>();
            for (String s : lore) {

                String finalLine = s
                        .replace("#poket-balance-graph#", pocketProgressBar);

                finalLore.add(Main.colorTranslation.hexTranslation(finalLine));

            }

            if (im.getLore() == null) {
                im.getLore().clear();
            }

            im.setLore(finalLore);
            is.setItemMeta(im);

            content.set(row, slot, ClickableItem.empty(is));
        }
    }

    @Override
    public void update(Player player, InventoryContents content) {

    }
}
