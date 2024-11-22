package wyattduber.cashapp.listeners.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SnifferBurgerListener implements Listener {

    private final Random rand = new Random();

    @EventHandler
    public void onSnifferDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Sniffer) {
            ItemStack beefDrop;

            ItemStack killItem = Objects.requireNonNull(event.getEntity().getKiller()).getInventory().getItemInMainHand();
            EntityDamageEvent.DamageCause lastDamageCause = Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause();
            boolean wasKilledByArrow = lastDamageCause.equals(EntityDamageEvent.DamageCause.PROJECTILE);
            boolean wasKilledByFire = lastDamageCause.equals(EntityDamageEvent.DamageCause.FIRE) || lastDamageCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK);
            if (killItem.containsEnchantment(Enchantment.FIRE_ASPECT) || (wasKilledByArrow && killItem.containsEnchantment(Enchantment.FLAME)) || wasKilledByFire) {
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
}
