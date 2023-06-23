package de.lightplugins.economy.enums;

import de.lightplugins.economy.master.Main;
import org.bukkit.configuration.file.FileConfiguration;

public enum MessagePath {

    Prefix("prefix"),
    NoPermission("noPermission"),
    Help("helpCommand"),
    Reload("reload"),
    PlayerNotFound("playerNotFound"),
    PlayerNotExists("playerNotExists"),
    MoneyBalance("moneyBalance"),
    NotANumber("notNumber"),
    NotZero("notZero"),
    OnlyPositivNumbers("onlyPostiv"),
    MoneyAddPlayer("moneyAddPlayer"),
    MoneyRemovePlayer("moneyRemovePlayer"),
    MoneySetPlayer("moneySetPlayer"),
    OnlyConsole("noPlayer"),
    PaySenderSuccess("paySenderOnSuccess"),
    PayTargetSuccess("payTargetOnSuccess"),
    PayFailed("payTransactionFailed"),
    PayCooldown("payCooldown"),
    PayEnabled("payToggleEnabled"),
    PayDisabled("payToggleDisabled"),
    NotYourself("notYourself"),
    NotHappening("payNotHappening"),
    WrongCommand("wrongCommand"),
    MoneyBalanceOther("moneyBalanceOther"),
    NotEnoughtMoney("notEnoughtMoney"),
    TransactionFailed("transactionFailed"),
    VoucherCreate("voucherCreate"),
    VoucherMinValue("voucherMinValue"),
    VoucherMaxValue("voucherMaxValue"),
    VoucherDisabled("voucherDisabled"),
    VoucherCollected("convertCollect"),
    VoucherOffHanad("voucherOffHanad"),
    BankUpgradeNoPermission("bankUpgradeNoPermission"),
    BankUpgradeAlreadyOwn("bankUpgradeAlreadyOwn"),
    BankUpgradeNeedPreviousLevel("bankUpgradeNeedPreviousLevel"),
    BankUpgradeNoMoney("bankUpgradeNoMoney"),
    BankUpgradeSuccess("bankUpgradeSuccess"),
    BankDepositValueViaChat("bankDepositValueViaChat"),
    BankDepositNotEnough("bankDepositNotEnough"),
    BankDepositNotPossible("bankDepositNotPossible"),
    BankDepositAllLimit("bankDepositAllLimit"),
    BankDepositAll("bankDepositAll"),
    BankWithdrawValueViaChat("bankWithdrawValueViaChat"),
    BankWithdrawAll("bankWithdrawAll"),
    BankWithdrawNotEnough("bankWithdrawNotEnough"),
    BankWithdrawSuccessfully("bankWithdrawSuccessfully"),
    BankDepositOverLimit("bankDepositOverLimit"),
    BankDepositSuccessfully("bankDepositSuccessfully"),
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
