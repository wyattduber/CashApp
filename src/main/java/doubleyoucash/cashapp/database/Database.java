package doubleyoucash.cashapp.database;

import doubleyoucash.cashapp.CashApp;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.time.*;

public class Database {

    private String dbPath;
    private final Connection dbcon;
    private final CashApp ca;

    public Database(String dbName) throws SQLException {
        ca = CashApp.getPlugin();
        dbPath = (ca.getDataFolder() + "/" + dbName);
        dbPath = "jdbc:sqlite:" + dbPath;
        dbcon = DriverManager.getConnection(dbPath);
        PreparedStatement stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS usernameSync(minecraftid TEXT NOT NULL, mcusername TEXT NOT NULL, discordid TEXT NOT NULL, isSynced BIT NOT NULL, syncReminder BIT NOT NULL)");
        stmt.execute();
        stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS wgAllowedPlaceBlocks(id INT IDENTITY(1,1) PRIMARY KEY, regionName STRING NOT NULL, regionId TEXT NOT NULL, block TEXT NOT NULL)");
        stmt.execute();
        stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS wgAllowedBreakBlocks(id INT IDENTITY(1,1) PRIMARY KEY, regionName STRING NOT NULL, regionId TEXT NOT NULL, block TEXT NOT NULL)");
        stmt.execute();
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

    /* WorldGuard Extension Methods */

    public void addAllowedPlaceBlock(String regionName, String regionId, String block) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO wgAllowedPlaceBlocks(regionName,regionId,block) VALUES (?,?,?)");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            stmt.setString(3, block);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding an allowed place block for region " + regionName + ":" + e.getMessage());
        }
    }

    public void addAllowedBreakBlock(String regionName, String regionId, String block) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO wgAllowedBreakBlocks(regionName,regionId,block) VALUES (?,?,?)");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            stmt.setString(3, block);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding an allowed break block for region " + regionName + ":" + e.getMessage());
        }
    }

    public boolean isBlockAllowedToPlace(String regionName, String regionId, String block) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT block FROM wgAllowedPlaceBlocks WHERE regionName=? AND regionId=? AND block=?");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            stmt.setString(3, block);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            ca.error("Error checking if block is allowed to be placed in region " + regionName + ":" + e.getMessage());
            return false;
        }
    }

    public boolean isBlockAllowedToBreak(String regionName, String regionId, String block) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT block FROM wgAllowedBreakBlocks WHERE regionName=? AND regionId=? AND block=?");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            stmt.setString(3, block);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            ca.error("Error checking if block is allowed to be broken in region " + regionName + ":" + e.getMessage());
            return false;
        }
    }

    public void removeAllowedPlaceBlock(String regionName, String regionId, String block) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("DELETE FROM wgAllowedPlaceBlocks WHERE regionName=? AND regionId=? AND block=?");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            stmt.setString(3, block);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error removing an allowed place block for region " + regionName + ":" + e.getMessage());
        }
    }

    public void removeAllowedBreakBlock(String regionName, String regionId, String block) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("DELETE FROM wgAllowedBreakBlocks WHERE regionName=? AND regionId=? AND block=?");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            stmt.setString(3, block);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error removing an allowed break block for region " + regionName + ":" + e.getMessage());
        }
    }

    public ArrayList<String> getAllowedPlaceBlocks(String regionName, String regionId) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT block FROM wgAllowedPlaceBlocks WHERE regionName=? AND regionId=?");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("block"));
            }
            return list;
        } catch (SQLException e) {
            ca.error("Error fetching allowed place blocks for region " + regionName + ":" + e.getMessage());
            return null;
        }
    }

    public ArrayList<String> getAllowedBreakBlocks(String regionName, String regionId) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT block FROM wgAllowedBreakBlocks WHERE regionName=? AND regionId=?");
            stmt.setString(1, regionName);
            stmt.setString(2, regionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("block"));
            }
            return list;
        } catch (SQLException e) {
            ca.error("Error fetching allowed break blocks for region " + regionName + ":" + e.getMessage());
            return null;
        }
    }

    /* Private Methods */

    private String getName(UUID minecraftID) {
        return Objects.requireNonNull(ca.getServer().getPlayer(minecraftID)).getName();
    }

}
