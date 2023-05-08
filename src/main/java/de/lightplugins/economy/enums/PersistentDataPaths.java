package de.lightplugins.economy.enums;

public enum PersistentDataPaths {

    MONEY_VALUE("money_value"),
    ;

    private String type;
    PersistentDataPaths(String type) { this.type = type; }
    public String getType() {

        return type;
    }
}
