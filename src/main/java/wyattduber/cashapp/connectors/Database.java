package wyattduber.cashapp.connectors;

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

        updateTableStructure("users",
                "CREATE TABLE users (" +
                        "minecraftid TEXT NOT NULL, " +
                        "mcusername TEXT NOT NULL, " +
                        "doNotDisturbState BIT NOT NULL)");

        updateTableStructure( "stallDescriptions",
                "CREATE TABLE stallDescriptions (" +
                        "stallID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "stallName TEXT NOT NULL, " +
                        "stallDescription TEXT NOT NULL, " +
                        "stallOwnerMinecraftID TEXT NOT NULL)");
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
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO users(minecraftid,mcusername,doNotDisturbState) VALUES (?,?,?)");
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setBoolean(3, false);
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding a user record for user " + player.getName() + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public boolean getDoNotDisturbStatus(Player player) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT doNotDisturbState FROM users WHERE minecraftid=?");
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            return rs.getBoolean("doNotDisturbState");
        } catch (SQLException e) {
            ca.error("Error retrieving Do not Disturb Status for user " + player.getName() + "!");
            ca.error("Error Message: " + e.getMessage());
            return false;
        }
    }

    public void setDoNotDisturbStatus(Player player, boolean status) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE users SET doNotDisturbState=? WHERE minecraftid=?");
            stmt.setBoolean(1, status);
            stmt.setString(2, player.getUniqueId().toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error updating Do Not Disturb Status for user " + player.getName() + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    /* Stall Description Methods */

    public void setStallDescription(String stallName, String stallDescription, UUID stallOwnerMinecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO stallDescriptions(stallName,stallDescription,stallOwnerMinecraftID) VALUES (?,?,?)");
            stmt.setString(1, stallName);
            stmt.setString(2, stallDescription);
            stmt.setString(3, stallOwnerMinecraftID.toString());
            stmt.execute();
        } catch (SQLException e) {
            ca.error("Error adding a stall description record for user " + getName(stallOwnerMinecraftID) + "!");
            ca.error("Error Message: " + e.getMessage());
        }
    }

    public String getStallDescription(String stallName) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT stallDescription FROM stallDescriptions WHERE stallName=?");
            stmt.setString(1, stallName);
            ResultSet rs = stmt.executeQuery();
            return rs.getString("stallDescription");
        } catch (SQLException e) {
            ca.error("Error fetching stall description for stall " + stallName + "!");
            ca.error("Error Message: " + e.getMessage());
            return null;
        }
    }

    public boolean updateStallDescription(String stallName, String newDescription, UUID stallOwnerMinecraftID) {
        try {
            if (!isStallInDatabase(stallName)) {
                setStallDescription(stallName, newDescription, stallOwnerMinecraftID);
            } else {
                PreparedStatement stmt = dbcon.prepareStatement("UPDATE stallDescriptions SET stallDescription=? WHERE stallName=?");
                stmt.setString(1, newDescription);
                stmt.setString(2, stallName);
                stmt.execute();
            }
            return true;
        } catch (SQLException e) {
            ca.error("Error updating stall description for stall " + stallName + "!");
            ca.error("Error Message: " + e.getMessage());
            return false;
        }
    }

    public boolean isStallInDatabase(String stallName) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT stallName FROM stallDescriptions WHERE stallName=?");
            stmt.setString(1, stallName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
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
