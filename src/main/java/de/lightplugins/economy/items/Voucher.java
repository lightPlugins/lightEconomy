package de.lightplugins.economy.items;

import de.lightplugins.economy.enums.PersistentDataPaths;
import de.lightplugins.economy.master.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Voucher {

    public ItemStack createVoucher(double itemValue, String creator) {

        Material material = Material.valueOf(Main.voucher.getConfig().getString("voucher.material"));
        boolean glow = Main.voucher.getConfig().getBoolean("voucher.glow");
        String displayname = Main.colorTranslation.hexTranslation(
                Objects.requireNonNull(Main.voucher.getConfig().getString("voucher.name"))
                        .replace("#amount#", String.valueOf(itemValue))
                        .replace("#currency#", Main.economyImplementer.currencyNameSingular()));

        ItemStack itemStack = new ItemStack(material, 1);

        itemStack.setType(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return new ItemStack(Material.STONE, 1);
        }

        itemMeta.setDisplayName(displayname);

        List<String> loreList = new ArrayList<>();

        Main.voucher.getConfig().getStringList("voucher.lore").forEach(lore -> {
            loreList.add(Main.colorTranslation.hexTranslation(lore)
                    .replace("#creator#", creator)
                    .replace("#amount#", String.valueOf(itemValue))
                    .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
        });

        if(itemMeta.hasLore()) {
            Objects.requireNonNull(itemMeta.getLore()).clear();
        }

        itemMeta.setLore(loreList);

        if(glow) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        /*
            Persistence Data for item value
         */

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey namespacedKeyValue = new NamespacedKey(
                Main.getInstance, PersistentDataPaths.MONEY_VALUE.getType());

        if(namespacedKeyValue.getKey().equalsIgnoreCase(PersistentDataPaths.MONEY_VALUE.getType())) {
            data.set(namespacedKeyValue, PersistentDataType.DOUBLE, itemValue);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;


    }
}
