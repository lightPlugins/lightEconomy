package de.lightplugins.economy.api.bank;

public class BankResponse {

    public enum BankResponseType {
        SUCCESS,
        FAIL_WITHDRAW,
        FAIL_DEPOSIT,
        FAIL_UNKNOWN
    }

    public final double amount;
    public final double balance;
    public final BankResponseType type;
    public final String errorMessage;
    public final String targetPlugin;

    public BankResponse(double amount, double balance, BankResponseType type, String errorMessage) {
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.errorMessage = errorMessage;
        this.targetPlugin = "lightEconomy";
    }

    public boolean transactionSuccess() {
        return type.equals(BankResponseType.SUCCESS);
    }
}
