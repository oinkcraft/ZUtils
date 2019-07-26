package zaphx.zutils.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zaphx.zutils.ZUtils;
import zaphx.zutils.managers.WarningFactory;
import zaphx.zutils.objects.UUIDFetcher;

import java.util.UUID;

import static org.bukkit.ChatColor.RED;
import static zaphx.zutils.objects.PermissionType.*;

public class WarningCommand implements CommandExecutor {

    private WarningFactory warningFactory;
    private ZUtils mainInstance;

    public WarningCommand(ZUtils zUtils) {
        this.mainInstance = zUtils;
        this.warningFactory = new WarningFactory(zUtils);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check if the user has permission to perform the command
        if (!sender.hasPermission(WARN_OTHERS.getPermission())) {
            sender.sendMessage(WARN_OTHERS.getRejectMessage());
            return true;
        }
        // Check if there are enough arguments to make a reason
        if (args.length < 3){
            sender.sendMessage(RED + "You did not provide enough arguments to perform that command!");
            return true;
        }
        // Get a reason
        String reason = warningFactory.getReason(args);
        // Get the warned player
        OfflinePlayer warnedPlayer = getPlayer(args[0]);

        if (warnedPlayer == null) {
            sender.sendMessage(RED + "That player has never played on the server before.");
            return true;
        } else if (!warnedPlayer.hasPlayedBefore()) {
            sender.sendMessage(RED + "That player has never played on the server before.");
            return true;
        }
        if (warnedPlayer.isOnline()) {
            Player onlinePlayer = warnedPlayer.getPlayer();
            sendBlank(5, onlinePlayer);
            warningFactory.sendWarning(onlinePlayer, sender, reason);
            sendBlank(3, onlinePlayer);
            warningFactory.logWarning(onlinePlayer, sender, reason);
        } else {
            warningFactory.sendWarningToSenderOnly(args[0], sender, reason);
            warningFactory.logWarning(warnedPlayer, sender, reason);
        }



        return true;
    }

    public OfflinePlayer getPlayer(String name) {
        OfflinePlayer player = mainInstance.getServer().getOfflinePlayer(UUIDFetcher.getUUID(name));
        if (player.hasPlayedBefore()) {
            return player;
        }
        return null;
    }

    public void sendBlank(int amount, Player player) {
        while (amount-- > 0) {
            player.sendMessage(" ");
        }
    }

}
