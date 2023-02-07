package de.lightplugins.lighteconomyv5.enums;

public enum PermissionPath {

    dummy("lighteconomy.dummy.dummy"),
    ;

    private final String path;
    PermissionPath(String path) { this.path = path; }
    public String getPerm() {
        return path;
    }
}
