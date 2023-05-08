package de.lightplugins.economy.utils;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class Util {

    /*  Send Message with Prefix to player  */

    public void sendMessage(Player player, String message) {
        String prefix = MessagePath.Prefix.getPath();
        player.sendMessage(Main.colorTranslation.hexTranslation(prefix + message));
    }
    /*  Send a message List to player without Prefix  */

    public void sendMessageList(Player player, List<String> list) {
        for(String s : list) {
            player.sendMessage(Main.colorTranslation.hexTranslation(s));
        }
    }

    public double fixDouble(double numberToFix) {
        BigDecimal bd = new BigDecimal(numberToFix).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public boolean isNumber(String number) {
        try {
            Double dummy = Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String formatDouble(double numberToFormat) {
        return String.format("%,.2f", numberToFormat);
    }

    /*  Count Animation with title for withdraw and deposit  */

    public void countUp(Player player, double endValue,
                        String upperLine, String lowerLine, String upperLineFinal, String lowerLineFinal) {

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
                    player.sendTitle(
                            upperLine.replace("#amount#", roundedOutput),
                            lowerLine.replace("#amount#", roundedOutput),
                            0, 20, 20);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, (float)0.7, (float)1.6);
                }

                if(roundedCountMin[0] >= endValue) {
                    BigDecimal bd4 = new BigDecimal(roundedSetPoint).setScale(2, RoundingMode.HALF_UP);
                    DecimalFormat formatter = new DecimalFormat("#,##0.00");
                    String roundedSetPointOutput = formatter.format(bd4);
                    player.sendTitle(
                            upperLineFinal.replace("#amount#", roundedSetPointOutput),
                            lowerLineFinal.replace("#amount#", roundedSetPointOutput),
                            0, 20, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, (float)0.7, (float)1.6);
                    this.cancel();

                }
            }
        }.runTaskTimer(Main.getInstance, 0, 1);
    }

    public boolean isInventoryEmpty(Player player) { return player.getInventory().firstEmpty() != -1; }
}
