package wyattduber.cashapp.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.UUID;

public class PlaceholderHandler extends PlaceholderExpansion {

    private final CashApp ca = CashApp.getPlugin();

    public PlaceholderHandler() {
        super();
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
     * GriefPrevention Addons: See griefPreventionPlaceholders() method
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
            case "griefprevention" -> {
                return griefPreventionPlaceholders(player, args);
            }
            default -> {
                return "";
            }
        }
    }

    /**
     * GriefPrevention Placeholder Handler
     * @param player The player requesting the placeholder
     * @param args The arguments provided with the placeholder
     * Placeholders:
     * %ca_griefprevention_stallowner%
     */

    private String griefPreventionPlaceholders(Player player, String[] args) {
        // Check if player is null
        if (player == null) return "";

        // Check if player is in a claim
        Claim playerClaim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
        if (playerClaim == null) return "";

        // Parse the arguments
        switch (args[1]) {
            case "stallowner" -> {
                var builders = new ArrayList<String>();
                playerClaim.getPermissions(builders, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                return Bukkit.getOfflinePlayer(UUID.fromString(builders.getFirst())).getName();
            }
            default -> {
                return "";
            }
        }
    }
}
