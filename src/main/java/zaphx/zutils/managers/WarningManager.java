package zaphx.zutils.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class WarningManager {

    private static WarningManager instance;

    private WarningManager() {
    }

    public void sendWarning(Player player, CommandSender sender, String reason) {
        player.sendMessage(GRAY + "You have been warned by " + sender.getName() + " for: "+ RED +reason);
        sender.sendMessage(GRAY + "You warned " + player.getName() + " for: "+ RED +reason);
    }

    public void checkAutoKick(Player warned) {

    }

    public static WarningManager getInstance() {
        return instance == null ? instance = new WarningManager() : instance;
    }

}
