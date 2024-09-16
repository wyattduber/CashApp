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

    public static String getClaimOwnerName(UUID claimOwner) {
        var playerClaim = getPlayerClaim(claimOwner);
        var builders = new ArrayList<String>();
        playerClaim.getPermissions(builders, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        return Bukkit.getOfflinePlayer(UUID.fromString(builders.getFirst())).getName();
    }

}