package wyattduber.cashapp.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.HashMap;

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

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
         // Check if player is null
        if (player == null) return "";

        ca.log("Identifier: " + identifier);

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

        // Try to find the claim in the world with that location
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claim == null) return "";

        ArrayList<String> owner = new ArrayList<>();
        claim.getPermissions(owner, null, null, null);

        return owner.get(0);
    }

    private Location createLocation(double x, double y, double z) {
        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

}
