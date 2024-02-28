package wyattduber.cashapp.items;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import java.util.Objects;

public class ItemListener implements Listener {

    @EventHandler
    public void onEggBreak(ThrownEggHatchEvent event) {
        if (event.getEgg().getShooter() instanceof Player) {
            if (Objects.requireNonNull(event.getEgg().getItem()).getItemMeta().equals(ItemManager.egg.getItemMeta())) {
                // Make sure that the egg never actually hatches
                event.setHatching(false);

                // Set off the explosion
                Location eggHitLocation = event.getEgg().getLocation();
                eggHitLocation.createExplosion(2);
            }
        }
    }

    @EventHandler
    public void onBowFire(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            if (Objects.requireNonNull(event.getBow()).getItemMeta().equals(ItemManager.bow.getItemMeta())) {
                Player player = (Player) event.getEntity();

                // Cancel the original arrow projectile
                event.setCancelled(true);

                // Replace arrow with a sped-up wither skull
                spawnWitherSkullProjectile(player);
            }
        }
    }

    @EventHandler
    public void onCrossbowFire(EntityLoadCrossbowEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Check if the item in the shooter's main hand is a crossbow
            if (event.getCrossbow().getItemMeta().equals(ItemManager.crossBow.getItemMeta())) {
                // Cancel the arrow shooting event
                event.setCancelled(true);

                // Replace arrow with a sped-up wither skull
                spawnWitherSkullProjectile(player);
            }
        }
    }

    private void spawnWitherSkullProjectile(Player player) {
        // Get the location and direction from player's eye location
        Vector direction = player.getEyeLocation().getDirection().multiply(1.5); // Adjust the speed as needed
        // Spawn a wither skull entity at player's eye location
        Entity witherSkull = player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.WITHER_SKULL);
        // Set the velocity of the wither skull entity
        witherSkull.setVelocity(direction);
    }

}
