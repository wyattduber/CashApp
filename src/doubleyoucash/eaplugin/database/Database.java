package doubleyoucash.eaplugin.database;

import doubleyoucash.eaplugin.CashApp;
import org.apache.commons.lang.ObjectUtils;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.*;

public class Database {

    private String dbPath;
    private Connection dbcon;
    private CashApp ca;

    public Database(String dbName) throws SQLException {
        ca = CashApp.getPlugin();
        dbPath = (ca.getDataFolder() + "/" + dbName);
        dbPath = "jdbc:sqlite:" + dbPath;
        dbcon = DriverManager.getConnection(dbPath);
        PreparedStatement stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS streaks(minecraftid TEXT NOT NULL, lastVote INT NOT NULL, streak INT NOT NULL, showStreak BIT NOT NULL)");
        stmt.execute();
        stmt = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS votes(id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, minecraftid TEXT NOT NULL, voteDate BIGINT NOT NULL, voteNum INT NOT NULL, site TEXT NOT NULL)");
        stmt.execute();
    }

    public String getDbPath() { return dbPath; }

    public void addUserStreakEntry(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO streaks(minecraftid,lastvote,streak) VALUES (?,?,?)");
            stmt.setString(1, minecraftID.toString());
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setInt(3, 1);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error inserting link for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public int getStreak(UUID minecraftID) {
        ResultSet rs;
        int streak;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT streak FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();
            streak = rs.getInt("streak");
            return streak;
        } catch (SQLException e) {
            ca.error("Error getting the streak for user " + getName(minecraftID) + "!");
            e.printStackTrace();
            return -1;
        }
    }

    public boolean getStreakStatus(UUID minecraftID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT showStreak FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();
            return rs.getBoolean("showStreak");
        } catch (SQLException e) {
            ca.error("Error getting showStreak boolean for user " + getName(minecraftID) + "!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleShowStreak(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE streaks SET showStreak=? WHERE minecraftid=?");
            stmt.setBoolean(1, !getStreakStatus(minecraftID));
            stmt.execute();
            return getStreakStatus(minecraftID);
        } catch (SQLException e) {
            ca.error("Error setting streak status for user " + getName(minecraftID) + "!");
            e.printStackTrace();
            return false;
        }
    }

    private void addDayToStreak(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE streaks SET streak=? WHERE minecraftid=?");
            stmt.setInt(1, getStreak(minecraftID));
            stmt.setString(2, minecraftID.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating streak for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public boolean userExistsInStreaks(UUID minecraftID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT streak FROM streaks WHERE minecraftid=?");
            rs = stmt.executeQuery();
            int streak = rs.getInt("streak");
            try {
                if (streak == 0) return false;
            } catch (NullPointerException e) {
                return false;
            }
            return true;
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
            stmt.setLong(1, System.currentTimeMillis());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating the last vote for user "  + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public long getLastVote(UUID minecraftID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT lastVote FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();
            return rs.getLong("lastVote");
        } catch (SQLException e) {
            ca.error("Error getting the last vote time for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public void insertVote(UUID minecraftID, int voteNum, String voteSite) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO votes(minecraftid,voteDate,voteNum,site) VALUES (?,?,?,?)");
            stmt.setString(1, minecraftID.toString());
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setInt(3, voteNum);
            stmt.setString(4, voteSite);
            stmt.execute();
            addDayToStreak(minecraftID);
        } catch (SQLException e) {
            ca.error("Error inserting a vote record for user " + getName(minecraftID) + "!");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getVotes(UUID minecraftID, int num) {
        ResultSet rs;
        ArrayList<String> list = new ArrayList<>();
        list.add("Votes for user " + getName(minecraftID) + " (Limit " + num + ")");
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT * FROM votes WHERE minecraftid=?");
            rs = stmt.executeQuery();
            for (int i = 0; i < num; i++) {
                var zonedDateTime = java.time.ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(rs.getLong("voteDate")), java.time.ZoneId.of("Germany/Berlin"));
                var formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss yyyy-M-d");
                String date = zonedDateTime.format(formatter);
                list.add("Date: " + date + "; Site: " + rs.getString("site") + "; ID: " + rs.getInt("voteNum"));
            }
            return list;
        } catch (SQLException e) {
            ca.error("Error retrieving user " + getName(minecraftID) + "'s vote data!");
            e.printStackTrace();
            return null;
        }
    }

    private String getName(UUID minecraftID) {
        return Objects.requireNonNull(ca.getServer().getPlayer(minecraftID)).getName();
    }

}
