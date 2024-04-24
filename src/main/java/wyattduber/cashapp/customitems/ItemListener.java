package wyattduber.cashapp.customitems;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ItemListener implements Listener {

    private final Random rand = new Random();

    @EventHandler
    public void onEggBreak(ThrownEggHatchEvent event) {
        if (event.getEgg().getShooter() instanceof Player) {
            if (Boolean.TRUE.equals(event.getEgg().getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN))) {
                // Make sure that the egg never actually hatches
                event.setHatching(false);

                // Set off the explosion
                Location eggHitLocation = event.getEgg().getLocation();
                eggHitLocation.createExplosion(4f, true, true);
            }
        }
    }

    @EventHandler
    public void onBowFire(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            if (Boolean.TRUE.equals(Objects.requireNonNull(event.getBow()).getItemMeta().getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN))) {
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
            if (Boolean.TRUE.equals(event.getCrossbow().getItemMeta().getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN))) {
                // Cancel the arrow shooting event
                event.setCancelled(true);

                // Replace arrow with a sped-up wither skull
                spawnWitherSkullProjectile(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item droppedItem) {
            ItemStack item = droppedItem.getItemStack();

            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN))) {
                if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_indestructible"), PersistentDataType.BOOLEAN))) {
                    event.setCancelled(true);
                }
            }
        }
        else if (event.getEntity() instanceof Player player) {
            if (event.getDamageSource() instanceof Explosive explosive) {
                if (explosive instanceof WitherSkull witherskull) {
                    if (witherskull.getShooter() instanceof ItemStack item) {
                        if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN))) {
                            if (item.getType() == Material.BOW || item.getType() == Material.CROSSBOW) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5, 2, false, true));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSnifferDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Sniffer) {
            ItemStack beefDrop;

            ItemStack killItem = Objects.requireNonNull(event.getEntity().getKiller()).getInventory().getItemInMainHand();
            boolean wasKilledByArrow = Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE);
            if (killItem.containsEnchantment(Enchantment.FIRE_ASPECT) || (wasKilledByArrow && killItem.containsEnchantment(Enchantment.ARROW_FIRE))) {
                beefDrop = new ItemStack(Material.COOKED_BEEF);
            } else {
                beefDrop = new ItemStack(Material.BEEF);
            }

            beefDrop.setAmount(rand.nextInt(2));

            ItemMeta meta = beefDrop.getItemMeta();
            meta.displayName(Component.text("Sniffer Meat", TextColor.fromHexString("#2fc087")));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Perfect for sniffer burgers!", TextColor.fromHexString("#a73b32")));
            meta.lore(lore);
            beefDrop.setItemMeta(meta);

            event.getDrops().add(beefDrop);
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
