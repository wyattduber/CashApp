package wyattduber.cashapp.customitems.items.trophies;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.List;

public class BrokenDrillBit {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.POINTED_DRIPSTONE, 64);
        ItemMeta meta = item.getItemMeta();

        // Set Name
        meta.displayName(Component.text("Broken Drill Bit", NamedTextColor.GRAY));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(CashApp.replaceColors("&8microwaved for 5 minutes, be careful")));
        meta.lore(lore);

        // Set Enchantment Glow
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set the finished modified meta and set the egg item to the created item
        item.setItemMeta(meta);
        return item;
    }

}
