package de.lightplugins.lighteconomyv5.utils;

import com.sun.org.apache.xerces.internal.xs.StringList;
import de.lightplugins.lighteconomyv5.enums.MessagePath;
import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.entity.Player;

import java.util.List;

public class Util {

    /*  Send Message with Prefix to player  */

    public void sendMessage(Player player, String message) {
        player.sendMessage(Main.colorTranslation.hexTranslation(MessagePath.Prefix.getPath()) + message);
    }
    /*  Send a message List to player without Prefix  */

    public void sendMessageList(Player player, List<String> list) {
        for(String s : list) {
            player.sendMessage(Main.colorTranslation.hexTranslation(s));
        }
    }

}
