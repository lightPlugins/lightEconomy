package de.lightplugins.economy.commands.tabcompletion;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BalanceTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull  Command command, String s, String[] args) {

        Player player = (Player) sender;

        if(args.length == 1) {

            if(!player.hasPermission(PermissionPath.MoneyOther.getPerm())) {
                return null;
            }

            MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);
            CompletableFuture<HashMap<String, Double>> test = moneyTableAsync.getPlayersBalanceList();

            List<String> arguments;

            try {
                HashMap<String, Double> finalPlayerList = test.get();
                arguments = new ArrayList<>(finalPlayerList.keySet());

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            return arguments;
        }

        return null;

    }
}
