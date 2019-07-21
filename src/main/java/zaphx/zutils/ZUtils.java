package zaphx.zutils;

import org.bukkit.plugin.java.JavaPlugin;

public final class ZUtils extends JavaPlugin {

    private static ZUtils instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ZUtils getInstance() {
        return instance == null ? instance = new ZUtils() : instance;
    }
}
