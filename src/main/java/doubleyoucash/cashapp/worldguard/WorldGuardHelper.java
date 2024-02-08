package doubleyoucash.cashapp.worldguard;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import doubleyoucash.cashapp.CashApp;

import java.util.HashMap;
import java.util.List;

public class WorldGuardHelper {

    private final CashApp ca;
    private final WorldGuard wg;
    private final RegionContainer regionContainer;
    private final HashMap<String, RegionManager> regions;

    public WorldGuardHelper(List<World> worlds) {
        ca = CashApp.getPlugin();
        wg = WorldGuard.getInstance();
        regionContainer = wg.getPlatform().getRegionContainer();

        // World setup for regions
        regions = new HashMap<>();

        for (World w : worlds) {
            regions.put(w.getName(), regionContainer.get(w));
        }
    }

    public void start() {
        ca.log("WorldGuardHelper started");
    }

    public boolean canPlayerBuildInRegion(String region, String worldName, String playerName) {
        ProtectedRegion reg;
        try {
            reg = regions.get(worldName).getRegion(region);
        } catch (Exception e) {
            ca.error("Error when getting region \"" + region + "\" in world \"" + worldName + "\": " + e.getMessage());
            return false;
        }

        return reg != null && (reg.isMember(playerName) || reg.isOwner(playerName));
    }

    public boolean isPlayerInRegion(String region, String worldName, int x, int y, int z) {
        ProtectedRegion reg;
        try {
            reg = regions.get(worldName).getRegion(region);
        } catch (Exception e) {
            ca.error("Error when getting region \"" + region + "\" in world \"" + worldName + "\": " + e.getMessage());
            return false;
        }

        return reg != null && reg.contains(x, y, z);
    }

}
