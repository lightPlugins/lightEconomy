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

public class BankSetCommand extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Set the amount of the Players Bank account, based on his Level";
    }

    @Override
    public String getSyntax() {
        return "/bank set <targetPlayer> <amount>";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {
        if(args.length == 3) {

            Sounds sounds = new Sounds();

            try {

                BankTableAsync bankTable = new BankTableAsync(Main.getInstance);
                BankLevelSystem bankLevelSystem = new BankLevelSystem(Main.getInstance);

                String targetName = args[1];
                double setValue = Double.parseDouble(args[2]);

                OfflinePlayer targetPlayer = Bukkit.getPlayer(targetName);

                if(!player.hasPermission(PermissionPath.BankSet.getPerm())) {
                    Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                if(targetPlayer == null) {
                    Main.util.sendMessage(player, MessagePath.PlayerNotExists.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                if(setValue <= 0) {
                    Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
                    return false;
                }

                double limit = bankLevelSystem.getLimitByLevel(targetPlayer.getUniqueId());



                if(limit < setValue) {
                    Main.util.sendMessage(player, MessagePath.BankSetPlayerLimit.getPath()
                            .replace("#limit#", Main.util.finalFormatDouble(limit)));
                    sounds.soundOnFailure(player);
                    return false;
                }

                CompletableFuture<Boolean> completableFuture = bankTable.setBankMoney(targetPlayer.getName(), setValue);

                try {

                    if(completableFuture.get()) {
                        Main.util.sendMessage(player, MessagePath.BankSetPlayer.getPath()
                                .replace("#amount#", Main.util.finalFormatDouble(setValue))
                                .replace("#currency#", Main.util.getCurrency(setValue))
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
        return false;

    }
}
