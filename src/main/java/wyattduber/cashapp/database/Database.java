package wyattduber.cashapp.database;

import wyattduber.cashapp.CashApp;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Database {

    private String dbPath;
    private final Connection dbcon;
    private final CashApp ca;

    public Database(String dbName) throws SQLException {
        ca = CashApp.getPlugin();
        dbPath = (ca.getDataFolder() + "/" + dbName);
        dbPath = "jdbc:sqlite:" + dbPath;
        dbcon = DriverManager.getConnection(dbPath);

        // Check if the table exists and update its structure if necessary
        updateTableStructure("usernameSync",
                "CREATE TABLE usernameSync (minecraftid TEXT NOT NULL, mcusername TEXT NOT NULL, discordid TEXT NOT NULL, isSynced BIT NOT NULL, syncReminder BIT NOT NULL)");

        }

    public String getDbPath() { return dbPath; }

    public boolean testConnection() {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT totalVotes FROM streaks");
            stmt.executeQuery();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /* Username Sync Methods */

    public void addSyncRecord(UUID minecraftid, String mcusername, long discordid, boolean isSynced) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO usernameSync(minecraftid,mcusername,discordid,isSynced) VALUES (?,?,?,?)");
            stmt.setString(1, minecraftid.toString());
            stmt.setString(2, mcusername);
            stmt.setString(3, Long.toString(discordid));
            stmt.setBoolean(4, isSynced);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding a sync record for user " + getName(minecraftid) + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public void updateSyncRecord(UUID minecraftid, String mcusername, long discordid, boolean isSynced) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE usernameSync SET mcusername=?,discordid=?,isSynced=? WHERE minecraftid=?");
            stmt.setString(1, mcusername);
            stmt.setString(2, Long.toString(discordid));
            stmt.setBoolean(3, isSynced);
            stmt.setString(4, minecraftid.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating a sync record for user " + getName(minecraftid) + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public boolean isSynced(UUID minecraftid) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT isSynced FROM usernameSync WHERE minecraftid=?");
            stmt.setString(1, minecraftid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.getBoolean("isSynced");
        } catch (SQLException e) {
            ca.error("Error fetching sync status for user " + getName(minecraftid) + "!");
            ca.error("Error Message: " + e.getMessage());
            return false;
        }
    }

    public boolean userExistsInSync(UUID minecraftid) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT minecraftid FROM usernameSync WHERE minecraftid=?");
            stmt.setString(1, minecraftid.toString());
            ResultSet rs = stmt.executeQuery();
            // Use rs.next() to check if there are any rows in the result set
            return rs.next();
        } catch (SQLException e) {
            ca.error("Error fetching sync status for user " + getName(minecraftid) + "!");
            ca.error("Error Message: " + e.getMessage());
            return false;
        }
    }

    public long getSyncedDiscordID(UUID minecraftid) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT discordid FROM usernameSync WHERE minecraftid=?");
            stmt.setString(1, minecraftid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("discordid");
            } else {
                // handle the case when no rows are returned
                return 0;
            }
        } catch (SQLException e) {
            ca.error("Error fetching synced Discord ID for user " + getName(minecraftid) + "!");
            ca.error("Error Message: " + e.getMessage());
            return 0;
        }
    }

    public boolean getSyncReminderStatus(UUID minecraftid) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT syncReminder FROM usernameSync WHERE minecraftid=?");
            stmt.setString(1, minecraftid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.getBoolean("syncReminder");
        } catch (SQLException e) {
            ca.error("Error fetching sync reminder status for user " + getName(minecraftid) + "!");
            ca.error("Error Message: " + e.getMessage());
            return false;
        }
    }

    public void setSyncReminderStatus(UUID minecraftid, boolean status) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE usernameSync SET syncReminder=? WHERE minecraftid=?");
            stmt.setBoolean(1, status);
            stmt.setString(2, minecraftid.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating sync reminder status for user " + getName(minecraftid) + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    /* Private Methods */

    private void updateTableStructure(String tableName, String createTableQuery) throws SQLException {
        DatabaseMetaData meta = dbcon.getMetaData();
        ResultSet rs = meta.getTables(null, null, tableName, null);
        if (rs.next()) {
            // Table exists, check if structure needs updating
            ResultSet columns = meta.getColumns(null, null, tableName, null);
            boolean needsUpdate = true;
            while (columns.next()) {
                // Check if all required columns exist
                if (!createTableQuery.contains(columns.getString("COLUMN_NAME"))) {
                    // Table structure doesn't match, alter the table
                    needsUpdate = false;
                    break;
                }
            }
            if (needsUpdate) {
                // Table structure is up to date
                if (ca.debugMode) ca.debug("Table '" + tableName + "' structure is up to date.");
            } else {
                // Table structure needs updating
                if (ca.debugMode) ca.debug("Updating table '" + tableName + "' structure...");
                Statement stmt = dbcon.createStatement();
                stmt.executeUpdate("DROP TABLE " + tableName);
                stmt.executeUpdate(createTableQuery);
                if (ca.debugMode) ca.debug("Table '" + tableName + "' structure updated successfully.");
            }
        } else {
            // Table does not exist, create it
            if (ca.debugMode) ca.debug("Creating table '" + tableName + "'...");
            Statement stmt = dbcon.createStatement();
            stmt.executeUpdate(createTableQuery);
            if (ca.debugMode) ca.debug("Table '" + tableName + "' created successfully.");
        }
    }

    private String getName(UUID minecraftID) {
        return Objects.requireNonNull(ca.getServer().getPlayer(minecraftID)).getName();
    }

}
