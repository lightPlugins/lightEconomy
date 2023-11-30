package de.lightplugins.economy.utils;
import de.lightplugins.economy.enums.MessagePath;
import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

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

    public String finalFormatDouble(double numberToRound) { return formatDouble(fixDouble(numberToRound));
    }

    /*  Count Animation with title for withdraw and deposit  */

    public void countUp(Player player, double endValue,
                        String upperLine, String lowerLine, String upperLineFinal, String lowerLineFinal) {

        double startValue = endValue * 0.05;

        FileConfiguration config = Main.settings.getConfig();

        Sound countUpSound = Sound.valueOf(
                Objects.requireNonNull(config.getString("settings.count-up-sound")).toUpperCase());
        Sound countFinishSound = Sound.valueOf(
                Objects.requireNonNull(config.getString("settings.final-count-sound")).toUpperCase());

        BigDecimal bd2 = new BigDecimal(startValue).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bd = new BigDecimal(endValue).setScale(2, RoundingMode.HALF_UP);

        double volume = config.getDouble("settings.volume");
        double pitch = config.getDouble("settings.pitch");


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
                    player.playSound(player.getLocation(), countUpSound, (float)volume, (float)pitch);
                }

                if(roundedCountMin[0] >= endValue) {
                    BigDecimal bd4 = new BigDecimal(roundedSetPoint).setScale(2, RoundingMode.HALF_UP);
                    DecimalFormat formatter = new DecimalFormat("#,##0.00");
                    String roundedSetPointOutput = formatter.format(bd4);
                    player.sendTitle(
                            upperLineFinal.replace("#amount#", roundedSetPointOutput),
                            lowerLineFinal.replace("#amount#", roundedSetPointOutput),
                            0, 20, 20);
                    player.playSound(player.getLocation(), countFinishSound, (float)volume, (float)pitch);
                    this.cancel();

                }
            }
        }.runTaskTimer(Main.getInstance, 0, 1);
    }

    public boolean isInventoryEmpty(Player player) { return player.getInventory().firstEmpty() != -1; }

    public double subtractPercentage(double originalValue, double percentage) {

        Bukkit.getLogger().log(Level.WARNING, "TEST 1 " + originalValue + " - " + percentage);

        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        return (percentage / 100) * originalValue;
    }

    public boolean checkPercentage(double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Percent value must be between 0 and 100");
        }
        Random random = new Random();
        double randomPercent = random.nextDouble() * 100;
        return randomPercent <= percent;
    }
}
