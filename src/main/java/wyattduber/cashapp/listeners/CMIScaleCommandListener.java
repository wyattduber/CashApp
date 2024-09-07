package wyattduber.cashapp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.PermissionAttachment;
import wyattduber.cashapp.CashApp;

import java.util.*;

public class CMIScaleCommandListener implements Listener {

    private final CashApp ca;

    public CMIScaleCommandListener() {
        ca = CashApp.getPlugin();
    }

    /*
     * Command = /cmi scale <set/add/take/clear> <player> <amount>
     */
    @EventHandler
    public void onCMIScaleCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        List<String> args = Arrays.asList(message.split(" "));
        if (args.size() <= 4) return;

        try {
            // Parse the scale value
            double scale = Double.parseDouble(args.get(3));

            // Validate scale range (0.5 to 5.0 in this example)
            if (scale < 0.5 || scale > 5.0) {
                ca.sendMessage(player, "&cScale must be between 0.5 and 5.0");
                event.setCancelled(true);
                return;
            }

            // Construct the permission node
            String permissionNode = "cmi.command.scale." + scale;

            // Check if the player has the permission
            if (!player.hasPermission(permissionNode)) {
                ca.sendMessage(player, "&cYou do not have permission to use this scale.");
                event.setCancelled(true);
            }

        } catch (NumberFormatException e) {
            // Handle the case where scale is not a valid number
            ca.sendMessage(player, "&cInvalid scale value. Please provide a number.");
            event.setCancelled(true);
        }
    }
}
