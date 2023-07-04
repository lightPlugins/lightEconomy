package de.lightplugins.economy.commands.bank;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.Sounds;
import de.lightplugins.economy.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BankSetLevelCommand extends SubCommand {
    @Override
    public String getName() {
        return "level";
    }

    @Override
    public String getDescription() {
        return "Set the players bank level to a fix number from the config";
    }

    @Override
    public String getSyntax() {
        return "/bank level set target 5";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 4) {
            //bank level set target 5

            Sounds sounds = new Sounds();
            BankTableAsync bankTable = new BankTableAsync(Main.getInstance);

            try {

                String targetName = args[2];
                OfflinePlayer target = Bukkit.getPlayer(targetName);

                if(target == null) {
                    Main.util.sendMessage(player, MessagePath.PlayerNotExists.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                if(!player.hasPermission(PermissionPath.BankSetLevel.getPerm())) {
                    Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                CompletableFuture<Integer> currentLevel = bankTable.playerCurrentBankLevel(target.getName());
                int currentBankLevel = currentLevel.get();
                int levelValue = Integer.parseInt(args[3]);

                if(levelValue <= 0) {
                    Main.util.sendMessage(player, MessagePath.BankSetLevelPlayerToLow.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                int maxLevelViaConfig = 0;
                FileConfiguration config = Main.bankLevelMenu.getConfig();


                for(int i = 0; i < config.getConfigurationSection("levels").getKeys(false).size(); i++) {
                    maxLevelViaConfig ++;
                }

                if(levelValue > maxLevelViaConfig) {
                    Main.util.sendMessage(player, MessagePath.BankSetLevelPlayerMax.getPath());
                    sounds.soundOnFailure(player);
                    return false;
                }

                CompletableFuture<Boolean> completableFuture = bankTable.setBankLevel(target.getName(), levelValue);

                try {

                    if(completableFuture.get()) {
                        Main.util.sendMessage(player, MessagePath.BankSetLevelPlayer.getPath()
                                .replace("#old-level#", String.valueOf(currentBankLevel))
                                .replace("#new-level#", String.valueOf(levelValue)));
                        sounds.soundOnSuccess(player);
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NumberFormatException e) {
                Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
                sounds.soundOnFailure(player);
                return false;
            }
        }

        return false;
    }
}
