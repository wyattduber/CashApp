package wyattduber.cashapp.customitems.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class WithersPenanceBow {

    public static ItemStack create() {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta meta = bow.getItemMeta();

        // Set Name
        meta.displayName(Component.text("Wither's Penance", NamedTextColor.DARK_GRAY));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("p e w"));
        meta.lore(lore);

        // Modify the NBT data so we know it's indestructible
        meta.getPersistentDataContainer().set(new NamespacedKey("CashApp", "ca_isAnarchyItem"), PersistentDataType.BOOLEAN, true);

        // Set the finished modified meta and set the egg item to the created item
        bow.setItemMeta(meta);

        // Set Enchantments
        bow.addUnsafeEnchantment(Enchantment.MENDING, 1);
        bow.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 6);
        bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

        return bow;
    }

}
