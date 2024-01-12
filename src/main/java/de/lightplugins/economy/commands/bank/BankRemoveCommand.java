package de.lightplugins.economy.commands.bank;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.BankLevelSystem;
import de.lightplugins.economy.utils.Sounds;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BankRemoveCommand extends SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Remove some money from a bank account from target player";
    }

    @Override
    public String getSyntax() {
        return "/bank remove <player> <amount>";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 3) {

            Sounds sounds = new Sounds();

            try {

                BankTableAsync bankTable = new BankTableAsync(Main.getInstance);

                String targetName = args[1];
                double removeValue = Double.parseDouble(args[2]);

                OfflinePlayer targetPlayer = Bukkit.getPlayer(targetName);

                if(!player.hasPermission(PermissionPath.BankRemove.getPerm())) {
                    Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                if(targetPlayer == null) {
                    Main.util.sendMessage(player, MessagePath.PlayerNotExists.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                CompletableFuture<Double> balanceFuture = bankTable.playerBankBalance(targetPlayer.getName());
                double currentBankBalance = balanceFuture.get();


                if(removeValue <= 0) {
                    Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                    return false;
                }


                double value = currentBankBalance - removeValue;

                if (value <= 0.0) {
                    removeValue = currentBankBalance;
                }

                CompletableFuture<Boolean> completableFuture = bankTable.setBankMoney(targetPlayer.getName(), currentBankBalance - removeValue);

                try {

                    if(completableFuture.get()) {
                        Main.util.sendMessage(player, MessagePath.BankRemovePlayer.getPath()
                                .replace("#amount#", Main.util.finalFormatDouble(removeValue))
                                .replace("#currency#", Main.util.getCurrency(removeValue))
                                .replace("#target#", targetName));
                        sounds.soundOnSuccess(player);
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (NumberFormatException ex) {
                Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
                sounds.soundOnFailure(player);
                return false;
            }
        }
        Main.util.sendMessage(player, MessagePath.WrongCommand.getPath()
                .replace("#command#", getSyntax()));
        return false;
    }
}
