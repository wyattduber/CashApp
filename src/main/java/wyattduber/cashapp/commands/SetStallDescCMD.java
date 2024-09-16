package wyattduber.cashapp.commands;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;
import wyattduber.cashapp.helpers.ChatMessageHelper;
import wyattduber.cashapp.helpers.TabCompleterHelper;
import wyattduber.cashapp.helpers.plugin.GriefPreventionHelper;
import wyattduber.cashapp.helpers.plugin.WorldGuardHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetStallDescCMD implements TabExecutor {

    private final CashApp ca = CashApp.getPlugin();
    private final Database db = ca.db;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        else {
            if (args.length < 2) return false;

            // Check if the stall is valid
            if (!ca.stalls.contains(args[0].toLowerCase())) {
                ChatMessageHelper.sendMessage(player, "&cInvalid stall! Please choose from the following list:", true);
                ChatMessageHelper.sendMessage(player, "&7" + String.join(", ", ca.stalls), false);
                return true;
            }

            // Check if the user is the main builder of the stall they are trying to set the description for
            Claim playerClaim = GriefPreventionHelper.getPlayerClaim(player.getUniqueId());
            if (playerClaim == null) {
                ChatMessageHelper.sendMessage(player, "&cYou must be standing in a stall claim to use this command!", true);
                return true;
            }
            if (!WorldGuardHelper.isPlayerInRegion(player, "newmall")) {
                ChatMessageHelper.sendMessage(player, "&cYou must be standing in the mall to use this command!", true);
                return true;
            }
            String builderName = GriefPreventionHelper.getClaimOwnerName(playerClaim);
            if (!player.getName().equalsIgnoreCase(builderName)) {
                ChatMessageHelper.sendMessage(player, "&cYou must be the owner of this stall to set the description!", true);
                return true;
            }

            // Set the stall description
            String stall = args[0];
            String desc = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            // Check if the description is too long
            db.updateStallDescription(stall, desc, player.getUniqueId());
            ChatMessageHelper.sendMessage(player, "Successfully set the description for stall &a" + stall.substring(0, 1).toUpperCase() + stall.substring(1) + "&f!", true);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.addAll(ca.stalls);
                tabs = TabCompleterHelper.narrowDownTabCompleteResults(args[0], tabs);
                yield tabs;
            }
            case 2 -> {
                tabs.add("Description...");
                yield tabs;
            }
            default -> tabs;
        };
    }
}
