package wyattduber.cashapp.customitems.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Eggsplosion {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.EGG, 64);
        ItemMeta meta = item.getItemMeta();

        // Set Name
        meta.displayName(Component.text("Eggsplosion", NamedTextColor.YELLOW));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("microwaved for 5 minutes, be careful", NamedTextColor.DARK_GRAY));
        meta.lore(lore);

        // Set Enchantment Glow
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Modify the NBT data so we know it's indestructible
        meta.getPersistentDataContainer().set(new NamespacedKey("CashApp", "ca_isAnarchyItem"), PersistentDataType.BOOLEAN, true);

        // Set the finished modified meta and set the egg item to the created item
        item.setItemMeta(meta);
        return item;
    }

}
