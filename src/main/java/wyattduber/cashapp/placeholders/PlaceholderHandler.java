package wyattduber.cashapp.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

import java.util.*;

public class PlaceholderHandler extends PlaceholderExpansion {

    private final CashApp ca;

    private final HashMap<String, Location> stalls = new HashMap<>();

    public PlaceholderHandler(CashApp ca) {
        super();
        this.ca = ca;

        /*
         * Declare map coordinates
         */

        // North
        stalls.put("north_1", createLocation(217, 78, -300));
        stalls.put("north_2", createLocation(239, 78, -300));
        stalls.put("north_3", createLocation(255, 78, -300));
        stalls.put("north_4", createLocation(277, 78, -300));

        // East
        stalls.put("east_1", createLocation(295, 76, -157));
        stalls.put("east_2", createLocation(295, 76, -178));
        stalls.put("east_3", createLocation(295, 76, -200));
        stalls.put("east_big", createLocation(295, 76, -222));
        stalls.put("east_4", createLocation(295, 76, -237));
        stalls.put("east_5", createLocation(295, 76, -259));
        stalls.put("east_6", createLocation(295, 76, -281));

        // South
        stalls.put("south_1", createLocation(277, 76, -140));
        stalls.put("south_2", createLocation(255, 76, -140));
        stalls.put("south_3", createLocation(239, 76, -140));
        stalls.put("south_4", createLocation(217, 76, -140));

        // West
        stalls.put("west_1", createLocation(199, 76, -158));
        stalls.put("west_2", createLocation(199, 76, -178));
        stalls.put("west_3", createLocation(199, 76, -200));
        stalls.put("west_big", createLocation(199, 76, -219));
        stalls.put("west_4", createLocation(199, 76, -237));
        stalls.put("west_5", createLocation(199, 76, -259));
        stalls.put("west_6", createLocation(199, 76, -281));

        // Small
        stalls.put("small_1", createLocation(230, 68, -274));
        stalls.put("small_2", createLocation(240, 68, -274));
        stalls.put("small_3", createLocation(254, 68, -274));
        stalls.put("small_4", createLocation(264, 68, -274));
        stalls.put("small_5", createLocation(271, 68, -267));
        stalls.put("small_6", createLocation(271, 68, -257));
        stalls.put("small_7", createLocation(271, 68, -249));
        stalls.put("small_8", createLocation(271, 68, -239));
        stalls.put("small_9", createLocation(271, 68, -198));
        stalls.put("small_10", createLocation(271, 68, -188));
        stalls.put("small_11", createLocation(271, 68, -180));
        stalls.put("small_12", createLocation(271, 68, -170));
        stalls.put("small_13", createLocation(264, 68, -162));
        stalls.put("small_14", createLocation(254, 68, -162));
        stalls.put("small_15", createLocation(240, 68, -162));
        stalls.put("small_16", createLocation(230, 68, -162));
        stalls.put("small_17", createLocation(221, 68, -170));
        stalls.put("small_18", createLocation(221, 68, -180));
        stalls.put("small_19", createLocation(221, 68, -188));
        stalls.put("small_20", createLocation(221, 68, -198));
        stalls.put("small_21", createLocation(221, 68, -239));
        stalls.put("small_22", createLocation(221, 68, -249));
        stalls.put("small_23", createLocation(221, 68, -257));
        stalls.put("small_24", createLocation(221, 68, -267));
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ca";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Wcash";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    /**
     * Placeholders:
     * ca_stall_[direction]_[number]
     * @param player
     * @param identifier
     * @return
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
         // Check if player is null
        if (player == null) return "";

        // Make entire string lowercase
        identifier = identifier.toLowerCase();

        // Split the placeholder requested into different arguments
        String[] args = identifier.split("_");

        // Parse the arguments
        switch (args[0]) {
            case "stall" -> {
                return parseStallPlaceholder(args);
            }
            default -> {
                return "";
            }

        }
    }

    private String parseStallPlaceholder(String[] args) {
        // Parse some args
        String direction = args[1];
        String number = args[2];

        // Get the location
        Location location = stalls.get(direction + "_" + number);
        if (location == null) return "";

        return getClaimOwner(location);
    }

    private Location createLocation(double x, double y, double z) {
        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

    private String getClaimOwner(Location location) {
        // Try to find the claim in the world with that location
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claim == null) return "Claim doesn't exist!";

        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);

        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(builders.get(0)));

            return player.getName();
        } catch (Exception e) {
            return "Empty";
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
