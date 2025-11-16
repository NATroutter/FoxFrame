package fi.natroutter.foxframe.permissions;

public interface PermissionHolder {
    INode bypassCommandCooldown();
    INode bypassButtonCooldown();
}
