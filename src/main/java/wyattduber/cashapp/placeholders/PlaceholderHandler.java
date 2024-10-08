package wyattduber.cashapp.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;
import wyattduber.cashapp.helpers.plugin.GriefPreventionHelper;

public class PlaceholderHandler extends PlaceholderExpansion {

    private final CashApp ca = CashApp.getPlugin();
    private final Database db = ca.db;

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
     * @return The value of the placeholder
     * Placeholders:
     * %ca_griefprevention_stallowner% - The owner of the stall
     * %ca_griefprevention_stalldescription_[stall]% - The custom description of the stall
     */

    private String griefPreventionPlaceholders(Player player, String[] args) {
        // Check if player is null
        if (player == null) return "";

        // Check if player is in a claim
        Claim playerClaim = GriefPreventionHelper.getPlayerClaim(player.getUniqueId());
        if (playerClaim == null) return "";

        // Parse the arguments
        switch (args[1]) {
            case "stallowner" -> {
                return GriefPreventionHelper.getClaimOwnerName(playerClaim);
            }
            case "stalldescription" -> {
                return retrieveStallDescription(args[2], playerClaim.getID());
            }
            default -> {
                return "";
            }
        }
    }

    private String retrieveStallDescription(String stall, long claimId) {
        // Check if the stall is valid
        if (!ca.stalls.contains(stall)) return "";

        // Retrieve the stall description
        var desc = db.getStallDescription(claimId, stall);
        if (desc.equals("null")) return "";
        return desc;
    }
}
