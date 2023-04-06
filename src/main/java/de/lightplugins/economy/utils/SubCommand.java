package de.lightplugins.economy.utils;

import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public abstract class SubCommand {

    public abstract String getName();

    public abstract  String getDescription();

    public abstract String getSyntax();

    public abstract boolean perform(Player player, String args[]) throws ExecutionException, InterruptedException;
}