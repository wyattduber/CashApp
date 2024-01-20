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
        PreparedStatement stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS streaks(minecraftid TEXT NOT NULL, totalVotes INT NOT NULL, lastVote BIGINT NOT NULL, streak INT NOT NULL, showStreak BIT NOT NULL)");
        stmt.execute();
        stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS votes(id INT IDENTITY(1,1) PRIMARY KEY, minecraftid TEXT NOT NULL, voteDate BIGINT NOT NULL, site TEXT NOT NULL)");
        stmt.execute();
        stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS usernameSync(minecraftid TEXT NOT NULL, mcusername TEXT NOT NULL, discordid TEXT NOT NULL, isSynced BIT NOT NULL)");
        stmt.execute();
    }

    public String getDbPath() { return dbPath; }

    /* Vote Streak Methods */

    public void addUserStreakEntry(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO streaks(minecraftid,totalVotes,lastVote,streak,showStreak) VALUES (?,?,?,?,?)");
            stmt.setString(1, minecraftID.toString());
            stmt.setInt(2, 1);
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setInt(4, 1);
            stmt.setInt(5, 1);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error inserting link for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public int getStreak(UUID minecraftID) {
        int streak;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT streak FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                streak = rs.getInt("streak");
                return streak;
            } else return -1;
        } catch (SQLException e) {
            //ca.error("Error getting the streak for user " + getName(minecraftID) + "!");
            //e.printStackTrace();
            return -1;
        }
    }

    public boolean getStreakStatus(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT showStreak FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("showStreak") == 1;
            } else {
                return false;
            }
        } catch (SQLException e) {
            ca.error("Error getting showStreak boolean for user " + getName(minecraftID) + "!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleShowStreak(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE streaks SET showStreak=? WHERE minecraftid=?");
            if (!getStreakStatus(minecraftID)) stmt.setInt(1, 1);
            else stmt.setInt(1, 0);
            stmt.setString(2, minecraftID.toString());
            stmt.execute();
            return getStreakStatus(minecraftID);
        } catch (SQLException e) {
            ca.error("Error setting streak status for user " + getName(minecraftID) + "!");
            e.printStackTrace();
            return false;
        }
    }

    public void addDayToStreak(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE streaks SET streak=? WHERE minecraftid=?");
            stmt.setInt(1, getStreak(minecraftID) + 1);
            stmt.setString(2, minecraftID.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating streak for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public boolean userExistsInStreaks(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT streak FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int streak = rs.getInt("streak");
                return streak != 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            ca.error("Error checking for user " + getName(minecraftID) + " in database!");
            e.printStackTrace();
            return false;
        }
    }

    public void resetStreak(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE streaks SET streak=? WHERE minecraftid=?");
            stmt.setInt(1, 1);
            stmt.setString(2, minecraftID.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error resetting streak for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public void updateLastVote(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE streaks SET lastVote=? WHERE minecraftid=?");
            long time = System.currentTimeMillis();
            stmt.setLong(1, time);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating the last vote for user "  + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public long getLastVoteTime(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT lastVote FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.getLong("lastVote");
        } catch (SQLException e) {
            //ca.error("Error getting the last vote time for user " + getName(minecraftID) + "!");
            //e.printStackTrace();
            return 0;
        }
    }

    public void insertVote(UUID minecraftID, String voteSite) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO votes(minecraftid,voteDate,site) VALUES (?,?,?)");
            stmt.setString(1, minecraftID.toString());
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setString(3, voteSite);
            stmt.execute();

            // Add new vote to total votes for this user
            addVoteToTotal(minecraftID);
        } catch (SQLException e) {
            ca.error("Error inserting a vote record for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getVotes(UUID minecraftID, int num) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Votes for user " + getName(minecraftID) + " (Limit " + num + ")");
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT * FROM votes WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            ResultSet rs = stmt.executeQuery();
            int i = 0;
            while (rs.next() && i < num) {
                i = addToList(rs, list, i);
            }
            return list;
        } catch (SQLException e) {
            ca.error("Error retrieving user " + getName(minecraftID) + "'s vote data!");
            e.printStackTrace();
            return null;
        }
    }

    private int addToList(ResultSet rs, ArrayList<String> list, int i) throws SQLException {
        var zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(rs.getLong("voteDate")), ZoneId.of("Europe/Paris"));
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy @ hh:mm:ss");
        String date = zonedDateTime.format(formatter);
        list.add(i + " - Date: " + date + "; Site: " + rs.getString("site"));
        i++;
        return i;
    }

    public ArrayList<String> getAllVotes(UUID minecraftID) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT * FROM votes WHERE minecraftid=?");
            ResultSet rs = stmt.executeQuery();
            int total = 0;
            while (rs.next()) {
                total = addToList(rs, list, total);
            }
            list.add(0, "Votes for user " + getName(minecraftID) + " (Limit " + total + ")");
            return list;
        } catch (SQLException e) {
            ca.error("Error retrieving user " + getName(minecraftID) + "'s vote data!");
            e.printStackTrace();
            return null;
        }
    }

    public int getTotalVotes(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT totalVotes FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalVotes");
            } else {
                // handle the case when no rows are returned
                return 0;
            }
        } catch (SQLException e) {
            ca.error("Error fetching total vote number for user " + getName(minecraftID) + "!");
            e.printStackTrace();
            return 0;
        }
    }

    public void addVoteToTotal(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE streaks SET totalVotes=? WHERE minecraftid=?");
            stmt.setInt(1, getTotalVotes(minecraftID) + 1);
            stmt.setString(2, minecraftID.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding a vote to the total votes for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void removeSyncRecord(UUID minecraftid) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("DELETE FROM usernameSync WHERE minecraftid=?");
            stmt.setString(1, minecraftid.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error removing a sync record for user " + getName(minecraftid) + "!");
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return 0;
        }
    }

    /* Private Methods */

    private String getName(UUID minecraftID) {
        return Objects.requireNonNull(ca.getServer().getPlayer(minecraftID)).getName();
    }

}
