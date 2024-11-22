package wyattduber.cashapp.listeners.item;

import com.Zrips.CMI.CMI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Random;

public class SnowGolemChristmasItemListener implements Listener {

    private final Random rand = new Random();
    private ItemStack christmasSnow;

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

    /**
     * ChristmasSnow:
     *     Item:
     *       v: 3955
     *       type: SNOWBALL
     *       meta:
     *         ==: ItemMeta
     *         meta-type: UNSPECIFIC
     *         display-name: '{"text":"","extra":[{"text":"ᴇ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#E4F3FF","bold":false},{"text":"ɴ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DFF1FF","bold":false},{"text":"ᴄ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DAEFFF","bold":false},{"text":"ʜ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#D5EDFF","bold":false},{"text":"ᴀ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#D0EBFF","bold":false},{"text":"ɴ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#CCE9FF","bold":false},{"text":"ᴛ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#C7E7FF","bold":false},{"text":"ᴇ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#C2E5FF","bold":false},{"text":"ᴅ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#BDE3FF","bold":false},{"text":"
     *           ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#B9E1FF","bold":false},{"text":"ѕ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#B4DFFF","bold":false},{"text":"ɴ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#AFDDFF","bold":false},{"text":"ᴏ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#AADBFF","bold":false},{"text":"ᴡ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#A6D9FF","bold":false}]}'
     *         lore:
     *         - '{"text":"","extra":[{"text":"Key item in","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"gray","bold":false}]}'
     *         - '{"text":"","extra":[{"text":"C","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#F96767","bold":false},{"text":"h","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DDDDDD","bold":false},{"text":"r","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#277E62","bold":false},{"text":"i","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DDDDDD","bold":false},{"text":"s","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#F96767","bold":false},{"text":"t","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DDDDDD","bold":false},{"text":"m","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#277E62","bold":false},{"text":"a","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DDDDDD","bold":false},{"text":"s
     *           ","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#F96767","bold":false},{"text":"E","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DDDDDD","bold":false},{"text":"v","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#F96767","bold":false},{"text":"e","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#277E62","bold":false},{"text":"n","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#F96767","bold":false},{"text":"t","obfuscated":false,"italic":false,"underlined":false,"strikethrough":false,"color":"#DDDDDD","bold":false}]}'
     *         enchantment-glint-override: true
     *
     */

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
            if (Boolean.TRUE.equals(Objects.requireNonNull(meta.getPersistentDataContainer().get(new NamespacedKey("cashapp", "ca_issnowgolemitem"), PersistentDataType.BOOLEAN)))) {
                // Cancel the snowball being thrown if it is part of the event
                event.setCancelled(true);
            }
        }
    }
}
