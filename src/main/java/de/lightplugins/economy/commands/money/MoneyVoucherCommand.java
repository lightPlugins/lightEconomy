package de.lightplugins.economy.commands.money;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.items.Voucher;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class MoneyVoucherCommand extends SubCommand {
    @Override
    public String getName() {
        return "voucher";
    }

    @Override
    public String getDescription() {
        return "Create a physical Voucher";
    }

    @Override
    public String getSyntax() {
        return "/le voucher create";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length != 3) {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            return false;
        }

        if(!player.hasPermission(PermissionPath.CreateVoucher.getPerm())) {
            Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
            return false;
        }

        if(!Main.voucher.getConfig().getBoolean("voucher.enable")) {
            Main.util.sendMessage(player, MessagePath.VoucherDisabled.getPath());
            return false;
        }

        if(args[1].equalsIgnoreCase("create")) {

            double playerBalance = Main.economyImplementer.getBalance(player.getName());
            double minValue = Main.voucher.getConfig().getDouble("voucher.min-value");
            double maxValue = Main.voucher.getConfig().getDouble("voucher.max-value");

            try {

                double itemValue = Double.parseDouble(args[2]);

                if(itemValue < 0) {
                    Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                    return false;
                }

                if(itemValue > maxValue) {
                    Main.util.sendMessage(player, MessagePath.VoucherMaxValue.getPath()
                            .replace("#max-value#", String.valueOf(maxValue))
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    return false;
                }

                if(itemValue < minValue) {
                    Main.util.sendMessage(player, MessagePath.VoucherMinValue.getPath()
                            .replace("#min-value#", String.valueOf(minValue))
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    return false;
                }

                if(itemValue > playerBalance) {
                    Main.util.sendMessage(player, MessagePath.NotEnoughtMoney.getPath());
                    return false;
                }

                EconomyResponse economyResponse = Main.economyImplementer.withdrawPlayer(player.getName(), itemValue);
                if(!economyResponse.transactionSuccess()) {
                    Main.util.sendMessage(player, MessagePath.TransactionFailed.getPath()
                            .replace("#reason#", economyResponse.errorMessage));
                    return false;
                }

                Voucher voucher = new Voucher();

                if(Main.util.isInventoryEmpty(player)) {
                    player.getInventory().addItem(voucher.createVoucher(itemValue, player.getName()));
                    Main.util.sendMessage(player, MessagePath.VoucherCreate.getPath()
                            .replace("#amount#", String.valueOf(itemValue))
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    return true;
                }

                Item item = player.getWorld().dropItem(player.getLocation(),
                        voucher.createVoucher(itemValue, player.getName()));

                item.setCustomName(Main.colorTranslation.hexTranslation(
                        Main.voucher.getConfig().getString("voucher.name"))
                        .replace("#amount#", args[2])
                        .replace("#currency#", Main.economyImplementer.currencyNameSingular()));

                item.setCustomNameVisible(true);
                Main.util.sendMessage(player, MessagePath.VoucherCreate.getPath());
                return true;

            } catch (NumberFormatException ex) {
                Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
                return false;
            }
        }

        return false;
    }
}
