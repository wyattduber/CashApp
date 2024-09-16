package wyattduber.cashapp.helpers.plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;
import wyattduber.cashapp.CashApp;

public class WorldGuardHelper {

    public static boolean isPlayerInRegion(Player player, String regionID) {
        // Get the WorldGuard instance
        WorldGuard worldGuard = WorldGuard.getInstance();

        // Get the player's current location and convert to WorldEdit's format
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation());

        // Get the region container
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();

        // Get the region manager for the player's world
        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));

        if (regions != null) {
            // Get the applicable regions at the player's location
            ApplicableRegionSet regionSet = regions.getApplicableRegions(loc.toVector().toBlockPoint());

            // Check if any of the regions match the specified region ID
            for (ProtectedRegion region : regionSet) {
                if (region.getId().equalsIgnoreCase(regionID)) {
                    return true;  // Player is in the region
                }
            }
        }

        return false;  // Player is not in the region
    }

}
