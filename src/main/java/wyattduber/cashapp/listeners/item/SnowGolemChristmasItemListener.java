package wyattduber.cashapp.listeners.item;

import com.Zrips.CMI.CMI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wyattduber.cashapp.CashApp;

import java.util.Random;

public class SnowGolemChristmasItemListener implements Listener {

    private final Random rand = new Random();
    private ItemStack christmasSnow;
    private CashApp ca = CashApp.getPlugin();

    public SnowGolemChristmasItemListener() {
        var savedItems = CMI.getInstance().getSavedItemManager().getSavedItems("dc");
        for (var savedItem : savedItems) {
            if (savedItem.getName().equals("ChristmasSnow")) {
                christmasSnow = savedItem.getItem();

                // Get Item Meta, set custom nbt tag, and re-add it to item
                ItemMeta meta = christmasSnow.getItemMeta();
                meta.getPersistentDataContainer().set(new NamespacedKey("cashapp", "ca_issnowgolemitem"), PersistentDataType.BOOLEAN, true);
                christmasSnow.setItemMeta(meta);
                break;
            }
        }
    }

    @EventHandler
    public void onSnowGolemDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Snowman) {
            if (rand.nextInt(10) == 1) { // 10% Chance
                event.getDrops().add(christmasSnow.clone());
            }
        }
    }

    @EventHandler
    public void onSnowBallThrow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball snowball) {
            ItemStack snowballItem = snowball.getItem();
            ItemMeta meta = snowballItem.getItemMeta();
            try {
                var nbtFlag = meta.getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_issnowgolemitem"), PersistentDataType.BOOLEAN);
                if (Boolean.TRUE.equals(nbtFlag)) {
                    // Cancel the snowball being thrown if it is part of the event
                    event.setCancelled(true);
                }
            } catch (NullPointerException ignored) {}
        }
    }
}
