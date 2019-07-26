package zaphx.zutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.RED;
import static zaphx.zutils.objects.PermissionType.*;

public class WarningCommand implements CommandExecutor {



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check if the user has permission to perform the command
        if (!sender.hasPermission(WARN_OTHERS.getPermission())) {
            sender.sendMessage(WARN_OTHERS.getRejectMessage());
            return true;
        }

        if (args.length < 3){
            sender.sendMessage(RED + "You did not provide enough arguments to perform that command!");
            return true;
        }

        return false;
    }
}
