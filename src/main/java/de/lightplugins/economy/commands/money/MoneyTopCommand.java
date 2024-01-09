package de.lightplugins.economy.commands.money;

import de.lightplugins.economy.database.querys.MoneyTableAsync;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import de.lightplugins.economy.utils.Sorter;
import de.lightplugins.economy.utils.SubCommand;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MoneyTopCommand extends SubCommand {
    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getDescription() {
        return "Shows the riches player on the server";
    }

    @Override
    public String getSyntax() {
        return "/money top";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {


        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("top")) {

                if(!player.hasPermission(PermissionPath.MoneyTop.getPerm())) {
                    Main.util.sendMessage(player, MessagePath.NoPermission.getPath());
                    return false;
                }

                FileConfiguration settings = Main.settings.getConfig();
                FileConfiguration message = Main.messages.getConfig();
                MoneyTableAsync moneyTableAsync = new MoneyTableAsync(Main.getInstance);

                List<String> exclude = new ArrayList<>(settings.getStringList("settings.baltop-exclude"));

                CompletableFuture<HashMap<String, Double>> futureMap = moneyTableAsync.getPlayersBalanceList();

                HashMap<String, Double> map = futureMap.get();

                for(String playername : exclude) {
                    map.remove(playername);
                }
                TreeMap<String, Double> list = (new Sorter(map)).get();

                CompletableFuture<HashMap<String, Double>> test = moneyTableAsync.getPlayersBalanceList();

                HashMap<String, Double> allPlayers = test.get();

                double allServerMoney = 0.0;

                for(Double single : allPlayers.values()) {
                    allServerMoney += single;
                }

                for(String header : message.getStringList("moneyTopHeader")) {
                    player.sendMessage(Main.colorTranslation.hexTranslation(header
                            .replace("#overall#", Main.util.finalFormatDouble(allServerMoney)))
                            .replace("#currency#", Main.util.getCurrency(allServerMoney)));

                }




                int baltopAmount = settings.getInt("settings.baltop-amount-of-players");

                for (int i = 0; i < baltopAmount; i++) {

                    try {
                        Map.Entry<String, Double> top = list.pollFirstEntry();
                        String name = top.getKey();
                        String confMessage = Main.colorTranslation.hexTranslation(message.getString("moneyTopFormat"))
                                .replace("#number#", String.valueOf(i + 1))
                                .replace("#name#", name)
                                .replace("#amount#", String.valueOf(Main.util.finalFormatDouble(top.getValue())))
                                .replace("#currency#", Main.util.getCurrency(top.getValue()));

                        String finalMessage = PlaceholderAPI.setPlaceholders(player, confMessage);
                        player.sendMessage(finalMessage);

                    } catch (Exception e) {
                        // Catch Exception for Map.Entry Exception if its null!
                    }
                }

                for (String footer : message.getStringList("moneyTopFooter")) {
                    player.sendMessage(Main.colorTranslation.hexTranslation(footer));
                }
            }


        } else {
            Main.util.sendMessage(player, MessagePath.WrongCommand.getPath());
            return false;
        }

        return false;
    }
}
