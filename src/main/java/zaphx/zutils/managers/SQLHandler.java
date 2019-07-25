package zaphx.zutils.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Language;
import org.mockito.internal.util.MockUtil;
import zaphx.zutils.ZUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SQLHandler {

    FileConfiguration config = ZUtils.getInstance().getConfig();
    /**
     * The instance of the SQLHandler
     */
    private static SQLHandler instance;
    /**
     * The SQL prefix
     */
    public final String prefix = config.getString("sql.prefix");
    /**
     * The port of the SQL server
     */
    private final int PORT = config.getInt("sql.port");
    /**
     * The username of the SQL server
     */
    private final String USERNAME = config.getString("sql.username");
    /**
     * The password of the SQL server
     */
    private final String PASSWORD = config.getString("sql.password");
    /**
     * The host of the SQL server
     */
    private final String HOST = config.getString("sql.host");
    /**
     * The database to use for the bot
     */
    private final String DATABASE = config.getString("sql.database");

    private Connection connection;

    /**
     * The default constructor
     */
    public SQLHandler(Connection connection) {
        this.connection = connection;
    }

    public SQLHandler() {
        this.connection = getConnection();
    }

    /**
     * Gets an SQL connection to the SQL server of the spigot server
     *
     * @return The connection to the SQL server the server uses
     */
    private Connection getConnection() {
        String driver = "com.mysql.jdbc.Driver";
        String url = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);

        try {
            // Check if driver exists
            Class.forName(driver);
            return DriverManager.getConnection(url + "?useSSL=false", USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.print("An error occurred while establishing connection to the SQL server. See stacktrace below for more information.");
            e.printStackTrace();
        }
        // Should never happen
        return null;
    }

    /**
     * Checks if a table exits
     *
     * @param tableName The table to look for
     * @return True if the table exists, else false
     */
    private boolean tableExist(String tableName) {
        Connection connection = this.connection == null ? this.getConnection() : this.connection;
        boolean tExists = false;
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equals(config.getString("sql.prefix") + tableName)) {
                    tExists = true;
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.print("An error occurred while checking if a table exists in your database. See stacktrace below for more information.");
            e.printStackTrace();
        }
        // Close connection to prevent too many open connections
        return tExists;
    }

    /**
     * Executes an SQL statement
     *
     * @param sql
     * @param parameters
     */
    public void executeStatementAndPost(@Language("sql") String sql, Object... parameters) {
        Future<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = this.connection == null ? this.getConnection() : this.connection;
                PreparedStatement ps = connection.prepareCall(String.format(sql, parameters));
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
        // always replace this -> ¼
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            if (!this.HOST.equals(""))
                e.printStackTrace();
        }
    }

    public void createWarningTableIfNotExist() {
        Future<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = this.connection == null ? this.getConnection() : this.connection;
                PreparedStatement ps = connection.prepareCall("CREATE TABLE IF NOT EXISTS ?warnings (ticket INTEGER UNSIGNED NOT NULL PRIMARY KEY, uuid VARCHAR(255) NOT NULL, warning_date DATE NOT NULL, reason VARCHAR(255) NOT NULL, warnee_uuid VARCHAR(255) NOT NULL)");
                ps.setString(1, prefix);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            // Check if the instance is a mock object. if it is, we don't want to print the stacktrace, as it is inevitable
            if (!MockUtil.isMock(this))
                e.printStackTrace();
        }
    }

    /**
     * Count the entries in the warning table.
     *
     * @return The amount of entries in the table
     */
    public long countTickets() {
        Future<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = this.connection == null ? this.getConnection() : this.connection;
                PreparedStatement ps = connection.prepareCall("SELECT COUNT(ticket) AS size FROM " + prefix + "warnings");
                List<Long> list = new ArrayList<>();
                ResultSet set = ps.executeQuery();
                if (set == null)
                    return 0L;
                while (set.next()) {
                    list.add(set.getLong("size"));
                }
                return list.get(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0L;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            // Check if the instance is a mock object. if it is, we don't want to print the stacktrace, as it is inevitable
            if (!MockUtil.isMock(this))
                e.printStackTrace();
        }
        return 0L;

    }

    /**
     * Count the entries in the warning table for a player using their UUID.
     *
     * @param player The player to count tickets for
     * @return The amount of entries in the table for the player
     */
    public Long countTickets(Player player) {
        Future<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = this.connection == null ? this.getConnection() : this.connection;
                PreparedStatement ps = connection.prepareCall("SELECT COUNT(ticket) AS size FROM " + prefix + "warnings WHERE uuid = '" + player.getUniqueId() + "'");
                List<Long> list = new ArrayList<>();
                ResultSet set = ps.executeQuery();
                if (set == null)
                    return 0L;
                while (set.next()) {
                    list.add(set.getLong("size"));
                }
                return list.get(0);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0L;
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            // Check if the instance is a mock object. if it is, we don't want to print the stacktrace, as it is inevitable
            if (!MockUtil.isMock(this))
                e.printStackTrace();
        }
        return 0L;

    }

    public boolean closeConnection() {
        try {
            this.connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
