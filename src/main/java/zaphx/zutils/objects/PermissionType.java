package zaphx.zutils.objects;

import static org.bukkit.ChatColor.RED;

public enum PermissionType {

    WARN_OTHERS("zutils.warn.other"),
    WARN_VIEW("zutils.warn.view"),
    ;


    private String permissionMissingMessage = RED + "You do not have permission to perform that command!";

    String permission;
    PermissionType(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getRejectMessage() {
        return permissionMissingMessage;
    }
}
