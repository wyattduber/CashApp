package wyattduber.cashapp.items;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import wyattduber.cashapp.CashApp;

import java.util.Objects;

public class ItemListener implements Listener {

    private final CashApp ca;

    public ItemListener() {
        ca = CashApp.getPlugin();
    }

    @EventHandler
    public void onEggBreak(ThrownEggHatchEvent event) {
        if (event.getEgg().getShooter() instanceof Player) {
            if (Objects.requireNonNull(event.getEgg().getItem()).getItemMeta().equals(ItemManager.egg.getItemMeta())) {
                // Get Player
                Player player = (Player) event.getEgg().getShooter();
                player.sendMessage(ca.replaceColors("&cBOOOOOM!"));

                // Make sure that the egg never actually hatches
                event.setHatching(false);

                // Set off the explosion
                Location eggHitLocation = event.getEgg().getLocation();
                eggHitLocation.createExplosion(2);
            }
        }
    }

}
