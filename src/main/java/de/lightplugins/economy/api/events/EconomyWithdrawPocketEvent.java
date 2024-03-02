package de.lightplugins.economy.api.events;

import de.lightplugins.economy.api.enums.TransactionStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EconomyWithdrawPocketEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    String target;
    double amount;
    boolean isCancelled;
    TransactionStatus transactionStatus;

    public EconomyWithdrawPocketEvent(String target, double amount) {
        this.target = target;
        this.amount = amount;
    }

    /**
     * Get the player
     * @return the player
     */
    public String getTargetAccountName() {
        return target;
    }

    /**
     * Get the amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Retrieves the transaction status.
     *
     * @return the transaction status
     */
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public double setAmount(double newAmount) {
        return amount = newAmount;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    /**
     * Get the list of event handlers for this event
     * @return the list of event handlers
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the list of registered handlers for this event.
     *
     * @return the list of registered handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Check if the task has been cancelled.
     *
     * @return true if the task has been cancelled, false otherwise
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Set the cancelled status of the object.
     *
     * @param cancelled the value to set for the cancelled status
     */
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

}
