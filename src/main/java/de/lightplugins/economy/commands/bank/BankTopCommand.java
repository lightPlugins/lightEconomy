package de.lightplugins.economy.commands.bank;

import de.lightplugins.economy.database.querys.BankTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.Sorter;
import de.lightplugins.economy.utils.SubCommand;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BankTopCommand extends SubCommand {
    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getDescription() {
        return "shows the top leaderboard from bank wallets";
    }

    @Override
    public String getSyntax() {
        return "/bank top";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(args.length == 1) {

            if(!player.hasPermission(PermissionPath.BankTop.getPerm())) {
                Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                return false;
            }

            FileConfiguration settings = Main.settings.getConfig();
            FileConfiguration message = Main.messages.getConfig();
            BankTableAsync bankTableAsync = new BankTableAsync(Main.getInstance);

            List<String> exclude = new ArrayList<>(settings.getStringList("settings.baltop-exclude"));

            CompletableFuture<HashMap<String, Double>> futureMap = bankTableAsync.getPlayersBalanceList();

            HashMap<String, Double> map = futureMap.get();


            for(String playername : exclude) {
                map.remove(playername);
            }

            TreeMap<String, Double> list = (new Sorter(map)).get();

            double allServerMoney = 0.0;


            for(Double single : map.values()) {
                allServerMoney += single;
            }

            for(String header : message.getStringList("bankTopHeader")) {
                player.sendMessage(Main.colorTranslation.hexTranslation(header
                                .replace("#overall#", Main.util.finalFormatDouble(allServerMoney)))
                        .replace("#currency#", Main.util.getCurrency(allServerMoney)));

            }

            int baltopAmount = settings.getInt("settings.baltop-amount-of-players");

            for (int i = 0; i < baltopAmount; i++) {

                try {
                    Map.Entry<String, Double> top = list.pollFirstEntry();
                    String name = top.getKey();
                    OfflinePlayer offlinePlayer = Bukkit.getPlayer(name);
                    String confMessage = Objects.requireNonNull(message.getString("bankTopFormat"))
                            .replace("#number#", String.valueOf(i + 1))
                            .replace("#name#", name)
                            .replace("#amount#", String.valueOf(Main.util.finalFormatDouble(top.getValue())))
                            .replace("#currency#", Main.util.getCurrency(top.getValue()));

                    String finalMessage = PlaceholderAPI.setPlaceholders(offlinePlayer, confMessage);
                    player.sendMessage(Main.colorTranslation.hexTranslation(finalMessage));

                } catch (Exception e) {
                   //throw new RuntimeException("Something went wrong on bank top map", e);
                }
            }

            for (String footer : message.getStringList("bankTopFooter")) {
                String finalMessage = PlaceholderAPI.setPlaceholders(player, footer);
                player.sendMessage(Main.colorTranslation.hexTranslation(finalMessage));
            }

        } else {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath()
                    .replace("#command#", getSyntax()));
            return false;
        }
        return false;
    }
}
