package de.lightplugins.lighteconomyv5.enums;

public enum PermissionPath {

    /*
        Admin Command Perissions
     */

    MoneyAdd("lighteconomy.admin.command.moneyadd"),
    MoneyRemove("lighteconomy.admin.command.moneyremove"),
    MoneySet("lighteconomy.admin.command.moneyset"),
    Reload("lighteconomy.admin.command.reload"),


    /*
        User Command Perissions
     */

    PayCommand("lighteconomy.user.command.pay"),
    MoneyTop("lighteconomy.user.command.top"),
    ;

    private final String path;
    PermissionPath(String path) { this.path = path; }
    public String getPerm() {
        return path;
    }
}
