package de.lightplugins.lighteconomyv5.utils;

import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TitleAnimator {


    public void countUp(Player player, double endValue) {

        double startValue = endValue * 0.05;

        BigDecimal bd2 = new BigDecimal(startValue).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bd = new BigDecimal(endValue).setScale(2, RoundingMode.HALF_UP);


        double roundedSetPoint = bd.doubleValue();
        final double[] roundedCountMin = {bd2.doubleValue()};

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if(roundedCountMin[0] < roundedSetPoint) {

                    BigDecimal bd3 = BigDecimal.valueOf(roundedCountMin[0]).setScale(2, RoundingMode.HALF_UP);
                    DecimalFormat formatter = new DecimalFormat("#,##0.00");
                    String roundedOutput = formatter.format(bd3);
                    roundedCountMin[0] += ((0.01 + roundedSetPoint - roundedCountMin[0])/2)/2;
                    player.sendTitle("", "TEST " + roundedOutput, 0, 20, 20);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, (float)0.7, (float)1.6);
                }

                if(roundedCountMin[0] >= endValue) {
                    BigDecimal bd4 = new BigDecimal(roundedSetPoint).setScale(2, RoundingMode.HALF_UP);
                    DecimalFormat formatter = new DecimalFormat("#,##0.00");
                    String roundedSetPointOutput = formatter.format(bd4);
                    player.sendTitle("", "TEST " + roundedSetPointOutput, 0, 20, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, (float)0.7, (float)1.6);
                    this.cancel();

                }
            }
        }.runTaskTimer(Main.getInstance, 0, 1);
    }

}
