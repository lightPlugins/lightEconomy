package de.lightplugins.economy.api.bank;

import de.lightplugins.economy.master.Main;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BankImplementer implements Bank {
    @Override
    public boolean isEnabled() {
        return Main.settings.getConfig().getBoolean("settings.enable-bank-feature");
    }

    @Override
    public CompletableFuture<Boolean> hasAccount(UUID uuid) {
        return Main.getInstance.bankTable.playerBankBalance(uuid)
                .thenApplyAsync(Objects::nonNull);
    }

    public CompletableFuture<BankResponse> createAccount(UUID uuid) {
        return Main.getInstance.bankTable.createBankAccount(uuid)
                .thenApplyAsync(success -> {
                    if (success) {
                        return new BankResponse(0.0, 0.0, BankResponse.BankResponseType.SUCCESS,
                                "[lightEconomy] Created bank account for " + uuid.toString());
                    } else {
                        return new BankResponse(0.0, 0.0, BankResponse.BankResponseType.FAIL_UNKNOWN,
                                "[lightEconomy] Failed while creating bank account for " + uuid.toString());
                    }
                });
    }

    @Override
    public CompletableFuture<Integer> getBankLevel(UUID uuid) {
        return null;
    }

    @Override
    public double getMaxBankValueByLevel(int bankLevel) {
        return 0;
    }

    @Override
    public int getMaxBankLevel() {
        return 0;
    }

    @Override
    public CompletableFuture<Boolean> hasEnough(UUID uuid, double amount) {
        return Main.getInstance.bankTable.playerBankBalance(uuid)
                .thenApplyAsync(res -> res >= amount);
    }

    @Override
    public CompletableFuture<Double> getBalance(UUID uuid) {
        return Main.getInstance.bankTable.playerBankBalance(uuid)
                .thenApply(balance -> balance);
    }

    @Override
    public CompletableFuture<BankResponse> withdraw(UUID uuid, double amount) {

        if(amount < 0) {
            return CompletableFuture.completedFuture(new BankResponse(0.0, 0.0, BankResponse.BankResponseType.FAIL_WITHDRAW,
                    "[lightEconomy] Value cant be negative for account " + uuid.toString()));
        }




        return null;
    }

    @Override
    public CompletableFuture<BankResponse> deposit(UUID uuid, double amount) {
        return null;
    }
}
