package de.lightplugins.economy.enums;

public enum PersistenceDataPaths {

    MONEY_VALUE("money_value"),
    ;

    private String type;
    PersistenceDataPaths(String type) { this.type = type; }
    public String getType() {

        return type;
    }
}
