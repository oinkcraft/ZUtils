package zaphx.zutils;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import zaphx.zutils.commands.WarningCommand;
import zaphx.zutils.managers.SQLHandler;

import java.io.File;

public class ZUtils extends JavaPlugin {

    private static ZUtils instance;
    public SQLHandler sqlHandler;
    public boolean isTest = false;
    public boolean isDizcordPresent = false;

    /**
     * This constructor is meant for the server to call. Do not use it in production
     */
    public ZUtils() {
        super();
    }

    /**
     * This constructor is only meant for testing purpose. It serves no real purpose in production, and should never be
     * called, as this will create unexpected behaviour.
     * @param loader A mock of the JavaPluginLoader, to simulate a server loading the plugin.
     * @param description A mock of the PluginDescriptionFile, to simulate the instance of a plugin.yml file.
     * @param dataFolder A mock of the plugin data folder, in case you're working with files
     * @param file A mock file simulating the config
     */
    public  ZUtils(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("Dizcord") != null) {
            isDizcordPresent = !isDizcordPresent;
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // Check if the SQL handler is null. If not we're in a test environment and do not need to set it as this is being done elsewbere
        if (!isTest) {
            sqlHandler = new SQLHandler();
            getCommand("warn").setExecutor(new WarningCommand(this));
        }



        sqlHandler.createWarningTableIfNotExist();




    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ZUtils getInstance() {
        return instance;
    }
}
