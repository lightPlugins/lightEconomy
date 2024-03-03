package de.lightplugins.economy.api.bank;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Bank {

    boolean isEnabled();
    CompletableFuture<Boolean> hasAccount(UUID uuid);
    CompletableFuture<BankResponse> createAccount(UUID uuid);
    CompletableFuture<Integer> getBankLevel(UUID uuid);
    double getMaxBankValueByLevel(int bankLevel);
    int getMaxBankLevel();
    CompletableFuture<Boolean> hasEnough(UUID uuid, double amount);
    CompletableFuture<Double> getBalance(UUID uuid);
    CompletableFuture<BankResponse> withdraw(UUID uuid, double amount);
    CompletableFuture<BankResponse> deposit(UUID uuid, double amount);

}
