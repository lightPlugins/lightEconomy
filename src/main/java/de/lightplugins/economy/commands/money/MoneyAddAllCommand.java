package de.lightplugins.economy.commands.money;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MoneyAddAllCommand extends SubCommand {
    @Override
    public String getName() {
        return "addall";
    }

    @Override
    public String getDescription() {
        return "Add all player in the database money";
    }

    @Override
    public String getSyntax() {
        return "/money addall [AMOUNT]";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length != 2) {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            return true;
        }

        if(!player.hasPermission(PermissionPath.MoneyAddAll.getPerm())) {
            Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
            return false;
        }

        if(!Main.util.isNumber(args[1])) {
            Main.util.sendMessage(player, MessagePath.NotANumber.getPath());
            return false;
        }

        double amount = Double.parseDouble(args[1]);

        if(amount == 0) {
            Main.util.sendMessage(player, MessagePath.NotZero.getPath());
            return false;
        }

        if(amount < 0) {
            Main.util.sendMessage(player, MessagePath.OnlyPositivNumbers.getPath());
            return false;
        }

        MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
        CompletableFuture<HashMap<String, Double>> futureMap = moneyTableAsync.getPlayersBalanceList();

        HashMap<String, Double> map = futureMap.get();

        int successCounter = 0;

        for (Map.Entry<String, Double> entry : map.entrySet()) {

            EconomyResponse respo = Main.economyImplementer.depositPlayer(entry.getKey(), amount);

            // Maybe too much spam if many players fail

            if(!respo.transactionSuccess()) {
                Main.util.sendMessage(player, MessagePath.TransactionFailed.getPath()
                        .replace("#reason#", respo.errorMessage + " - " + entry.getKey()));
            }

            successCounter++;

            Player target = Bukkit.getPlayer(entry.getKey());
            assert target != null;
            Main.util.sendMessage(target, MessagePath.moneyAddAllTarget.getPath()
                    .replace("#sender#", player.getName())
                    .replace("#amount#", args[1])
                    .replace("#currency#", Main.economyImplementer.currencyNameSingular()));

        }

        Main.util.sendMessage(player, MessagePath.moneyAddAll.getPath()
                .replace("#successcount#", String.valueOf(successCounter))
                .replace("#overallcount#", String.valueOf(map.size()))
                .replace("#amount#", Main.util.finalFormatDouble(amount))
                .replace("#currency#", Main.economyImplementer.currencyNameSingular()));

        if(successCounter < map.size()) {
            Main.util.sendMessage(player, MessagePath.moneyAddAllFailed.getPath()
                    .replace("#failcount#", String.valueOf(map.size() - successCounter)));
        }

        return false;
    }
}
