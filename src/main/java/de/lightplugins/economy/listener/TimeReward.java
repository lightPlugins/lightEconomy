package de.lightplugins.economy.listener;

import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.enums.PermissionPath;
import de.lightplugins.economy.master.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;


public class TimeReward {

    public void startTimedReward() {

        FileConfiguration settings = Main.settings.getConfig();
        String currencyNamePlural = Main.economyImplementer.currencyNameSingular();
        String currencyNameSingular = Main.economyImplementer.currencyNameSingular();
        boolean enabledMoney = settings.getBoolean("settings.timeReward.money.enable");
        double moneyAmount = settings.getDouble("settings.timeReward.money.amount");
        int intervalSeconds = settings.getInt("settings.timeReward.money.intervall");
        int intervallInMinutes = intervalSeconds / 60;


        new BukkitRunnable(){

            @Override
            public void run() {

                if(!enabledMoney) {
                    return;
                }

                long currentMillisInSeconds = System.currentTimeMillis() / 1000;

                if(currentMillisInSeconds % intervalSeconds == 0) {
                    Bukkit.getServer().getOnlinePlayers().forEach(singlePlayer -> {

                        if(singlePlayer == null) {
                            return;
                        }

                        EconomyResponse economyResponse =
                                Main.economyImplementer.depositPlayer(singlePlayer, moneyAmount);

                        if(economyResponse.transactionSuccess()) {

                            Main.util.sendMessage(singlePlayer, MessagePath.TimeRewardMoney.getPath()
                                    .replace("#amount#", String.valueOf(moneyAmount))
                                    .replace("#currency#",
                                            moneyAmount > 1 ? currencyNamePlural : currencyNameSingular)
                                    .replace("#intervallMinutes#", String.valueOf(intervallInMinutes))
                                    .replace("#intervallSeconds#", String.valueOf(intervalSeconds)));
                        }
                    });
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance, 0, 20);

    }
}
