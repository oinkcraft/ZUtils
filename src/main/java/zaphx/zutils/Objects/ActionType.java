package zaphx.zutils.Objects;

public enum ActionType {

    WARNING("WARNING"),
    BAN("BAN"),
    TEMP_BAN("TEMP_BAN"),
    KICK("KICK");

    String actionType;
    ActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return this.actionType;
    }
}
