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

public class WithersPenanceCrossbow {

    public static ItemStack create() {
        ItemStack crossBow = new ItemStack(Material.CROSSBOW, 1);
        ItemMeta meta = crossBow.getItemMeta();

        // Set Name
        meta.displayName(Component.text("Wither's Penance", NamedTextColor.DARK_GRAY));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("p e w"));
        meta.lore(lore);

        // Modify the NBT data so we know it's indestructible
        meta.getPersistentDataContainer().set(new NamespacedKey("CashApp", "ca_isAnarchyItem"), PersistentDataType.BOOLEAN, true);

        // Set the finished modified meta and set the egg item to the created item
        crossBow.setItemMeta(meta);

        // Set Enchantments
        crossBow.addUnsafeEnchantment(Enchantment.PIERCING, 6);
        crossBow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 4);
        crossBow.addUnsafeEnchantment(Enchantment.MENDING, 1);
        crossBow.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
        crossBow.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

        return crossBow;
    }

}
