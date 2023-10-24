package de.lightplugins.economy.database.querys;

import de.lightplugins.economy.master.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerTable {

    public Main plugin;
    public PlayerTable(Main plugin) {
        this.plugin = plugin;
    }
    private final String tableName = "PlayerData";

    public CompletableFuture<Boolean> alreadyTrusted(String uuid) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid=?");
                ps.setString(1, uuid);

                ResultSet rs = ps.executeQuery();

                return rs.next();

            } catch (SQLException e) {
                e.printStackTrace();
                return null;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public CompletableFuture<List<String>> getTrustedBanks(String uuid) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid=?");
                ps.setString(1, uuid);

                ResultSet rs = ps.executeQuery();
                List<String> uuidList = new ArrayList<>();

                while(rs.next()) {
                    uuidList.add(rs.getString("trustedBank"));
                }

                return uuidList;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public CompletableFuture<List<String>> getOwnTruster(String uuid) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE trustedBank=?");
                ps.setString(1, uuid);

                ResultSet rs = ps.executeQuery();
                List<String> uuidList = new ArrayList<>();

                while(rs.next()) {
                    uuidList.add(rs.getString("uuid"));
                }

                return uuidList;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public CompletableFuture<Boolean> addTrustedPlayerTo(String uuid, String targetBankAccountUserUUID) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection = null;
            PreparedStatement ps = null;

            try {

                connection = plugin.ds.getConnection();

                ps = connection.prepareStatement("INSERT INTO " + tableName + " (uuid,trustedBank) VALUES (?,?)");
                ps.setString(1, uuid);
                ps.setString(2, targetBankAccountUserUUID);
                ps.execute();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;

            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
