package de.lightplugins.economy.inventories;

import de.lightplugins.economy.master.Main;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        for(String singleElement : mainMenuConfig.getStringList("inventory")) {

            int slot = mainMenuConfig.getInt(singleElement + ".slot");
            String name = Main.colorTranslation.hexTranslation(mainMenuConfig.getString(singleElement + ".name"));
            Material material = Material.valueOf(mainMenuConfig.getString(singleElement + ".material"));
            List<String> lore = mainMenuConfig.getStringList(singleElement + ".lore");

        }
    }

    @Override
    public void update(Player player, InventoryContents content) {

    }
}
