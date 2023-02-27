package doubleyoucash.eaplugin.database;

import doubleyoucash.eaplugin.CashApp;

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
    }

    public String getDbPath() { return dbPath; }

    public void addUserStreakEntry(UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO streaks(minecraftid,totalVotes,lastvote,streak,showStreak) VALUES (?,?,?,?,?)");
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
        ResultSet rs;
        int streak;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT streak FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();
            streak = rs.getInt("streak");
            return streak;
        } catch (SQLException e) {
            //ca.error("Error getting the streak for user " + getName(minecraftID) + "!");
            //e.printStackTrace();
            return -1;
        }
    }

    public boolean getStreakStatus(UUID minecraftID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT showStreak FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();
            return rs.getInt("showStreak") == 1;
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
            try {
                int streak = rs.getInt("streak");
                if (streak == 0) return false;
            } catch (NullPointerException | SQLException e) {
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
            long time = System.currentTimeMillis();
            printDate(minecraftID, time);
            stmt.setLong(1, time);
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
            int i = 0;
            ca.log(String.valueOf(rs.getInt("id")));
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
        var zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(rs.getLong("voteDate")), ZoneId.of("Germany/Berlin"));
        var formatter = DateTimeFormatter.ofPattern("hh:mm:ss yyyy-M-d");
        String date = zonedDateTime.format(formatter);
        list.add("Date: " + date + "; Site: " + rs.getString("site"));
        ca.log("Date: " + date + "; Site: " + rs.getString("site"));
        i++;
        return i;
    }

    public ArrayList<String> getAllVotes(UUID minecraftID) {
        ResultSet rs;
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT * FROM votes WHERE minecraftid=?");
            rs = stmt.executeQuery();
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
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT totalVotes FROM streaks WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();
            return rs.getInt("totalVotes");
        } catch (SQLException e) {
            ca.error("Error fetching total vote number for user " + getName(minecraftID) + "!");
            e.printStackTrace();
            return 0;
        }
    }

    private String getName(UUID minecraftID) {
        return Objects.requireNonNull(ca.getServer().getPlayer(minecraftID)).getName();
    }

    private void printDate(UUID id, long date) {
        var zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("Germany/Berlin"));
        var formatter = DateTimeFormatter.ofPattern("hh:mm:ss yyyy-M-d");
        String result = zonedDateTime.format(formatter);
        ca.log(id + "'s last vote is at " + result);
    }

}
