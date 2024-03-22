package wyattduber.cashapp.customitems.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WithersPenanceBow {

    public static ItemStack create() {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();

        // Set Name
        bowMeta.displayName(Component.text("Wither's Penance", NamedTextColor.DARK_GRAY));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("p e w"));
        bowMeta.lore(lore);

        // Set the finished modified meta and set the egg item to the created item
        bow.setItemMeta(bowMeta);

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
