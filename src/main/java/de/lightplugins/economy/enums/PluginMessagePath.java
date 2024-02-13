package de.lightplugins.economy.enums;

public enum PluginMessagePath {

    PAY("lighteconomy:pay"),
    ;

    private String type;
    PluginMessagePath(String type) { this.type = type; }
    public String getType() {

        return type;
    }
}
