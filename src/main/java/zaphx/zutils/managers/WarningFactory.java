package zaphx.zutils.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zaphx.zutils.objects.ActionType;
import zaphx.zutils.ZUtils;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class WarningFactory {

    private SQLHandler sqlHandler;
    public final int KICK_LIMIT;
    public final int BAN_LIMIT;

    public WarningFactory(ZUtils instance) {
        KICK_LIMIT = instance.getConfig().getInt("warning.autokick.warning-limit", 5);
        BAN_LIMIT = instance.getConfig().getInt("warning.autoban.warning-limit", 10);
        sqlHandler = new SQLHandler();
    }

    public WarningFactory(ZUtils instance, SQLHandler sqlHandler) {
        KICK_LIMIT = instance.getConfig().getInt("warning.autokick.warning-limit", 5);
        BAN_LIMIT = instance.getConfig().getInt("warning.autoban.warning-limit", 10);
        this.sqlHandler = sqlHandler;
    }

    public void updateTicket(Player player, CommandSender sender, String reason) {
        sqlHandler.executeStatementAndPost("INSERT INTO %swarnings (uuid, warning_date, reason, warnee_uuid) VALUES ('%s', '%s', '%s', '%s')",
                sqlHandler.prefix,
                player.getUniqueId(),
                reason,
                sender.getName().equalsIgnoreCase("console") ? sender.getName(): ((Player) sender).getUniqueId());
    }

    public void updateTicket(OfflinePlayer player, CommandSender sender, String reason) {
        sqlHandler.executeStatementAndPost("INSERT INTO %swarnings (uuid, warning_date, reason, warnee_uuid) VALUES ('%s', '%s', '%s', '%s')",
                sqlHandler.prefix,
                player.getUniqueId(),
                reason,
                sender.getName().equalsIgnoreCase("console") ? sender.getName(): ((Player) sender).getUniqueId());
    }

    public void sendWarning(Player player, CommandSender sender, String reason) {
        player.sendMessage(GRAY + "You have been warned by " + sender.getName() + " for: " + RED + reason);
        sender.sendMessage(GRAY + "You warned " + player.getName() + " for: " + RED + reason);
    }

    public long getAmountOfWarnings(Player player) {
        return sqlHandler.countTickets(player);
    }

    public boolean hasWarningLimit(ActionType actionType, Player player) {
        if (actionType.getActionType().equals(ActionType.KICK.getActionType())) {
            return this.getAmountOfWarnings(player) >= KICK_LIMIT;
        } else if (actionType.getActionType().equals(ActionType.BAN.getActionType())) {
            return this.getAmountOfWarnings(player) >= BAN_LIMIT;
        } else {
            throw new IllegalArgumentException("The actiontype provided did not match KICK or BAN");
        }
    }

    public void autoKick(Player warned) {
        if (ZUtils.getInstance().getConfig().getBoolean("warning.autokick.enabled")) {
            warned.kickPlayer(RED + ZUtils.getInstance().getConfig().getString("warning.autokick.message"));
        }
    }
}
