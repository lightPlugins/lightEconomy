package de.lightplugins.lighteconomyv5.commands.money;

import de.lightplugins.lighteconomyv5.database.querys.MoneyTableAsync;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.enums.PermissionPath;
import de.lightplugins.lighteconomyv5.master.Main;
import de.lightplugins.lighteconomyv5.utils.Sorter;
import de.lightplugins.lighteconomyv5.utils.SubCommand;
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

                for(String header : message.getStringList("moneyTopHeader")) {
                    player.sendMessage(Main.colorTranslation.hexTranslation(header));

                }

                int baltopAmount = settings.getInt("settings.baltop-amount-of-players");

                for (int i = 0; i < baltopAmount; i++) {

                    try {
                        Map.Entry<String, Double> top = list.pollFirstEntry();
                        String name = top.getKey();

                        player.sendMessage(Main.colorTranslation.hexTranslation(message.getString("moneyTopFormat"))
                                .replace("#number#", String.valueOf(i + 1))
                                .replace("#name#", name)
                                .replace("#amount#", String.valueOf(Main.util.fixDouble(top.getValue())))
                                .replace("#currency#", Main.economyImplementer.currencyNameSingular()));
                    } catch (Exception e) {
                        // Catch Exception for Map.Entry Exception if its null!
                    }
                }

                for (String footer : message.getStringList("moneyTopFooter")) {
                    player.sendMessage(Main.colorTranslation.hexTranslation(footer));
                }
            }
        }

        return false;
    }
}
