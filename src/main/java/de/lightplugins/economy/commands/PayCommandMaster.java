package de.lightplugins.economy.commands;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PayCommandMaster implements CommandExecutor {

    private final List<String> cooldown = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(sender instanceof Player) {

            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("toggle")) {
                    if(Main.payToggle.contains(sender.getName())) {
                        Main.payToggle.remove(sender.getName());
                        Main.util.sendMessage((Player) sender, MessagePath.PayEnabled.getPath());
                        return false;
                    }
                    Main.payToggle.add(sender.getName());
                    Main.util.sendMessage((Player) sender, MessagePath.PayDisabled.getPath());
                    return false;
                }

                Main.util.sendMessage((Player) sender, MessagePath.PayWrongCommand.getPath());
                return false;
            }

            if(args.length == 2) {

                if(!sender.hasPermission(PermissionPath.PayCommand.getPerm())) {
                    Main.util.sendMessage((Player) sender, MessagePath.NoPermission.getPath());
                    return false;
                }

                if(cooldown.contains(sender.getName())) {
                    Main.util.sendMessage((Player) sender, MessagePath.PayCooldown.getPath());
                    return false;
                }

                String target = args[0];

                if(!Main.util.isNumber(args[1])) {
                    Main.util.sendMessage((Player) sender, MessagePath.NotANumber.getPath());
                    return false;
                }

                double amount = Double.parseDouble(args[1]);

                if(!Main.economyImplementer.hasAccount(target)) {
                    Main.util.sendMessage((Player) sender, MessagePath.PlayerNotExists.getPath());
                    return false;
                }
                if(target.equalsIgnoreCase(sender.getName())) {
                    Main.util.sendMessage((Player) sender, MessagePath.NotYourself.getPath());
                    return false;
                }
                if(amount < 0) {
                    Main.util.sendMessage((Player) sender, MessagePath.OnlyPositivNumbers.getPath());
                    return false;
                }

                if(!Main.economyImplementer.has(sender.getName(), amount)) {
                    Main.util.sendMessage((Player) sender, MessagePath.PayFailed.getPath()
                            .replace("#reason#", "Not enough Money"));
                    return false;
                }
                if(Main.payToggle.contains(target)) {
                    Main.util.sendMessage((Player) sender, MessagePath.PayFailed.getPath()
                            .replace("#reason#", "Target disabled payments"));
                    return false;
                }

                EconomyResponse withdrawExecutor = Main.economyImplementer.withdrawPlayer(sender.getName(), amount);
                EconomyResponse depositTarget = Main.economyImplementer.depositPlayer(target, amount);

                if(withdrawExecutor.transactionSuccess() && depositTarget.transactionSuccess()) {

                    Main.util.sendMessage((Player) sender, MessagePath.PaySenderSuccess.getPath()
                            .replace("#amount#", Main.util.formatDouble(amount))
                            .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                            .replace("#target#", target));

                    if(Main.getInstance.isBungee) {
                        Main.util.sendMessageThrowBungeeNetwork((Player) sender, target, MessagePath.PayTargetSuccess.getPath()
                                .replace("#amount#", Main.util.formatDouble(amount))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                                .replace("#sender#", sender.getName()));
                    } else {
                        OfflinePlayer targetPlayer = Bukkit.getPlayer(target);
                        if(targetPlayer != null && targetPlayer.isOnline()) {
                            Main.util.sendMessage(Objects.requireNonNull(targetPlayer.getPlayer()),
                                    MessagePath.PayTargetSuccess.getPath()
                                            .replace("#amount#", Main.util.formatDouble(amount))
                                            .replace("#currency#", Main.economyImplementer.currencyNameSingular())
                                            .replace("#sender#", sender.getName()));
                        }
                    }

                    cooldown.add(sender.getName());

                    BukkitTask task = new BukkitRunnable() {

                        final int[] counter = {0};
                        @Override
                        public void run() {

                            if(counter[0] >= 5) {
                                cooldown.remove(sender.getName());
                                this.cancel();
                            }
                            counter[0] ++;

                        }
                    }.runTaskTimerAsynchronously(Main.getInstance, 0, 20);

                    return false;
                }

                if(withdrawExecutor.transactionSuccess() &! depositTarget.transactionSuccess() ) {
                    EconomyResponse moneyRedo = Main.economyImplementer.depositPlayer(sender.getName(), amount);

                    if(moneyRedo.transactionSuccess()) {
                        Main.util.sendMessage((Player) sender, MessagePath.PayFailed.getPath()
                                .replace("#reason#", depositTarget.errorMessage));
                        return false;
                    }

                    Main.util.sendMessage((Player) sender, MessagePath.NotHappening.getPath());
                    return false;

                }

                return false;
            }
        }
        Main.util.sendMessage((Player) sender, MessagePath.PayWrongCommand.getPath());
        return false;
    }
}
