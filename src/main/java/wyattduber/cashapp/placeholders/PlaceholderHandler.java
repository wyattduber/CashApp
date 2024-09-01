package wyattduber.cashapp.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.*;
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
     *
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
            default -> {
                return "";
            }
        }
    }

}
