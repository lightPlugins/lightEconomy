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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BankShowCommand extends SubCommand {
    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Shows bank stats from other players";
    }

    @Override
    public String getSyntax() {
        return "/bank show <player>";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        Sounds sounds = new Sounds();

        if(args.length == 2) {

            if(!player.hasPermission(PermissionPath.BankShow.getPerm())) {
                Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                sounds.soundOnFailure(player);
                return false;
            }

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[1]);

            if(offlinePlayer == null) {
                Main.util.sendMessage(player, MessagePath.PlayerNotFound.getPath());
                sounds.soundOnFailure(player);
                return false;
            }

            BankTableAsync bankTable = new BankTableAsync(Main.getInstance);
            BankLevelSystem bankLevelSystem = new BankLevelSystem(Main.getInstance);

            FileConfiguration config = Main.bankLevelMenu.getConfig();

            int maxLevelViaConfig = 0;

            for(int i = 0; i < config.getConfigurationSection("levels").getKeys(false).size(); i++) {
                maxLevelViaConfig ++;
            }

            CompletableFuture<Integer> currentLevelFuture = bankTable.playerCurrentBankLevel(offlinePlayer.getName());
            CompletableFuture<Double> currentBankBalanceFuture = bankTable.playerBankBalance(offlinePlayer.getName());

            int currentLevel = currentLevelFuture.get();
            double currentBankBalance = currentBankBalanceFuture.get();
            double currentLimit = bankLevelSystem.getLimitByLevel(offlinePlayer.getUniqueId());

            FileConfiguration messages = Main.messages.getConfig();

            for(String s : messages.getStringList("bankShowOther")) {

                player.sendMessage(Main.colorTranslation.hexTranslation(s
                        .replace("#target#", offlinePlayer.getName())
                        .replace("#bank-balance#", Main.util.finalFormatDouble(currentBankBalance))
                        .replace("#currency#", Main.economyImplementer.currencyNamePlural())
                        .replace("#limit-by-level#", Main.util.finalFormatDouble(currentLimit))
                        .replace("#current-bank-level#", String.valueOf(currentLevel))
                        .replace("#max-level#", String.valueOf(maxLevelViaConfig))));
            }

            return false;

        }

        return false;
    }
}
