package de.lightplugins.economy.enums;

public enum PermissionPath {

    /*
        Admin Command Perissions
     */

    MoneyAdd("lighteconomy.admin.command.moneyadd"),
    MoneyRemove("lighteconomy.admin.command.moneyremove"),
    MoneySet("lighteconomy.admin.command.moneyset"),
    Reload("lighteconomy.admin.command.reload"),
    Debug("lighteconomy.admin.command.debug"),
    MoneyOther("lighteconomy.admin.command.balanceother"),
    MoneyAddAll("lighteconomy.admin.command.moneyaddall"),
    BankAdd("lighteconomy.admin.command.bankadd"),
    BankOpenOther("lighteconomy.admin.command.bankopenother"),
    BankSet("lighteconomy.admin.command.bankset"),
    BankRemove("lighteconomy.admin.comand.bankremove"),
    BankSetLevel("lighteconomy.admin.command.banksetlevel"),
    BankShow("lighteconomy.admin.command.bankshow"),
    HelpCommandAdmin("lighteconomy.admin.command.helpadmin"),
    CreateNPC("lighteconomy.admin.command.npc.create"),

    /*
        User Command Perissions
     */

    PayCommand("lighteconomy.user.command.pay"),
    MoneyTop("lighteconomy.user.command.top"),
    CreateVoucher("lighteconomy.user.command.createvoucher"),
    BankOpen("lighteconomy.user.command.bank"),
    ;

    private final String path;
    PermissionPath(String path) { this.path = path; }
    public String getPerm() {
        return path;
    }
}
