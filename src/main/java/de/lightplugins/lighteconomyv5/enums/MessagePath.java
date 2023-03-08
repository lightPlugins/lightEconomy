package de.lightplugins.lighteconomyv5.enums;

import de.lightplugins.lighteconomyv5.master.Main;
import org.bukkit.configuration.file.FileConfiguration;

public enum MessagePath {

    Prefix("prefix"),
    NoPermission("noPermission"),
    Help("helpCommand"),
    PlayerNotFound("playerNotFound"),
    MoneyBalance("moneyBalance"),
    NotANumber("notNumber"),
    NotZero("notZero"),
    OnlyPositivNumbers("onlyPostiv"),
    MoneyAddPlayer("moneyAddPlayer"),
    MoneyRemovePlayer("moneyRemovePlayer"),
    MoneySetPlayer("moneySetPlayer"),
    ;

    private final String path;

    MessagePath(String path) { this.path = path; }
    public String getPath() {
        FileConfiguration paths = Main.messages.getConfig();
        try {
            return paths.getString(this.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }
}
