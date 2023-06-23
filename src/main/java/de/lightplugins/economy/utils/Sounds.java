package de.lightplugins.economy.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {

    public void soundOnSuccess(Player player) {
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, (float)1.0,(float)1.0);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, (float)1.0,(float)1.0);
    }

    public void soundOnFailure(Player player) {
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, (float)1.0,(float)0.3);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, (float)1.0,(float)0.5);
    }
}
