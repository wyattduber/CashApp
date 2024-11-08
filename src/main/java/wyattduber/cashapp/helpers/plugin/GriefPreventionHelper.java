package wyattduber.cashapp.helpers.plugin;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

public class GriefPreventionHelper {

    public static Claim getPlayerClaim(UUID playerUUID) {
        var player = Bukkit.getOfflinePlayer(playerUUID);
        return GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
    }

    public static String getClaimOwnerName(Claim claim) {
        var builders = new ArrayList<String>();
        claim.getPermissions(builders, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        return Bukkit.getOfflinePlayer(UUID.fromString(builders.getFirst())).getName();
    }

    public static Claim getClaimById(long claimId) {
        return GriefPrevention.instance.dataStore.getClaim(claimId);
    }

}
