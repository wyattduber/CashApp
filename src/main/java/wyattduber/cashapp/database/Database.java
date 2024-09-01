package wyattduber.cashapp.database;

import org.bukkit.entity.Player;
import wyattduber.cashapp.CashApp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
        updateTableStructure("tickets",
                "CREATE TABLE tickets (" +
                        "ticketID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "channelID TEXT NOT NULL, " +
                        "ownerMinecraftID TEXT NOT NULL, " +
                        "ticketAdminOnly BIT NOT NULL, " +
                        "ticketOpen BIT NOT NULL, " +
                        "ticketClosed BIT NOT NULL, " +
                        "ticketCreationReason TEXT NOT NULL, " +
                        "ticketCreationTime TEXT NOT NULL, " +
                        "ticketCloseReason TEXT NULL)");

        updateTableStructure("users",
                "CREATE TABLE users (" +
                        "minecraftid TEXT NOT NULL, " +
                        "mcusername TEXT NOT NULL, " +
                        "doNotDisturbState BIT NOT NULL)");
    }

    public String getDbPath() { return dbPath; }

    public boolean testConnection() {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT minecraftid FROM users LIMIT 1");
            stmt.executeQuery();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /* Users Methods */

    public boolean isUserInDatabase(Player player) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT minecraftid FROM users WHERE minecraftid=?");
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void insertUser(Player player) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO users(minecraftid,mcusername,doNotDisturbState) VALUES (?,?,?,?,?)");
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setBoolean(5, false);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding a user record for user " + player.getName() + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public boolean getDoNotDisturbStatus(Player player) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT doNotDisturbStatus FROM users WHERE minecraftid=?");
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            return rs.getBoolean("doNotDisturbStatus");
        } catch (SQLException e) {
            ca.error("Error retrieving Do not Disturb Status for user " + player.getName() + "!");
            ca.error("Error Message: " + e.getMessage());
            return false;
        }
    }

    public void setDoNotDisturbStatus(Player player, boolean status) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE users SET doNotDisturbStatus=? WHERE minecraftid=?");
            stmt.setBoolean(1, status);
            stmt.setString(2, player.getUniqueId().toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating Do Not Disturb Status for user " + player.getName() + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    /* Ticket Methods */

    public void openTicket(long channelID, UUID ownerMinecraftID, boolean ticketAdminOnly, String ticketCreationReason, String ticketCreationTime, String ticketCloseReason) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO tickets(channelID,ownerMinecraftID,ticketAdminOnly,ticketOpen,ticketClosed,ticketCreationReason,ticketCreationTime,ticketCloseReason) VALUES (?,?,?,?,?,?,?,?)");
            stmt.setString(1, Long.toString(channelID));
            stmt.setString(2, ownerMinecraftID.toString());
            stmt.setBoolean(3, ticketAdminOnly);
            stmt.setBoolean(4, true);
            stmt.setBoolean(5, false);
            stmt.setString(6, ticketCreationReason);
            stmt.setString(7, ticketCreationTime);
            stmt.setString(8, null);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding a ticket record for user " + getName(ownerMinecraftID) + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public void closeTicket(long channelID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE tickets SET ticketOpen=?,ticketClosed=? WHERE channelID=?");
            stmt.setBoolean(1, false);
            stmt.setBoolean(2, true);
            stmt.setString(3, Long.toString(channelID));
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error closing ticket " + channelID + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public void closeTicket(long channelID, String closeReason) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE tickets SET ticketOpen=?,ticketClosed=?,ticketCloseReason=? WHERE channelID=?");
            stmt.setBoolean(1, false);
            stmt.setBoolean(2, true);
            stmt.setString(3, closeReason);
            stmt.setString(4, Long.toString(channelID));
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error closing ticket " + channelID + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public List<Long> getOpenTickets() {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT channelID FROM tickets WHERE ticketOpen=?");
            stmt.setBoolean(1, true);
            ResultSet rs = stmt.executeQuery();
            List<Long> openTickets = new ArrayList<>();
            while (rs.next()) {
                openTickets.add(rs.getLong("channelID"));
            }
            return openTickets;
        } catch (SQLException e) {
            ca.error("Error fetching open tickets!");
            ca.error("Error Message: " + e.getMessage());
            return null;
        }
    }

    public boolean isTicketAdminOnly(long channelID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT ticketAdminOnly FROM tickets WHERE channelID=?");
            stmt.setString(1, Long.toString(channelID));
            ResultSet rs = stmt.executeQuery();
            return rs.getBoolean("ticketAdminOnly");
        } catch (SQLException e) {
            ca.error("Error fetching ticket admin only status for ticket " + channelID + "!");
            ca.error("Error Message: " + e.getMessage());
            return false;
        }
    }

    public void setAdminOnly(long channelID, boolean ticketAdminOnly) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE tickets SET ticketAdminOnly=? WHERE channelID=?");
            stmt.setBoolean(1, ticketAdminOnly);
            stmt.setString(2, Long.toString(channelID));
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating ticket admin only status for ticket " + channelID + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    /* Helper Methods */

    private void updateTableStructure(String tableName, String createTableQuery) throws SQLException {
        DatabaseMetaData meta = dbcon.getMetaData();
        ResultSet rs = meta.getTables(null, null, tableName, null);

        if (rs.next()) {
            // Table exists
            ResultSet columns = meta.getColumns(null, null, tableName, null);
            List<String> existingColumns = new ArrayList<>();

            while (columns.next()) {
                existingColumns.add(columns.getString("COLUMN_NAME"));
            }

            // Check for columns to add
            try (Statement stmt = dbcon.createStatement()) {
                String[] createTableParts = createTableQuery.split("\\(");
                String columnsPart = createTableParts[1];
                columnsPart = columnsPart.substring(0, columnsPart.length() - 1); // Remove trailing ')'
                String[] requiredColumns = columnsPart.split(",");

                for (String requiredColumn : requiredColumns) {
                    requiredColumn = requiredColumn.trim().split("\\s+")[0]; // Get only the column name
                    if (!existingColumns.contains(requiredColumn)) {
                        // Column doesn't exist, add it
                        stmt.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + requiredColumn);
                        if (ca.debugMode) ca.debug("Added column '" + requiredColumn + "' to table '" + tableName + "'.");
                    }
                }

                // Check for columns to remove
                for (String existingColumn : existingColumns) {
                    if (!createTableQuery.contains(existingColumn)) {
                        // Column exists in the table but not in the required structure, remove it
                        stmt.executeUpdate("ALTER TABLE " + tableName + " DROP COLUMN " + existingColumn);
                        if (ca.debugMode) ca.debug("Removed column '" + existingColumn + "' from table '" + tableName + "'.");
                    }
                }
            }
        } else {
            // Table does not exist, create it
            if (ca.debugMode) ca.debug("Creating table '" + tableName + "'...");
            try (Statement stmt = dbcon.createStatement()) {
                stmt.executeUpdate(createTableQuery);
                if (ca.debugMode) ca.debug("Table '" + tableName + "' created successfully.");
            }
        }
    }


    private String getName(UUID minecraftID) {
        return Objects.requireNonNull(ca.getServer().getPlayer(minecraftID)).getName();
    }

}
