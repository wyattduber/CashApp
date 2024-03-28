package wyattduber.cashapp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

public class LeashListener implements Listener {

    @EventHandler
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasPermission("ca.leashotherplayers")) {
                player.setLeashHolder(event.getLeashHolder());
            }
        }
    }

}
