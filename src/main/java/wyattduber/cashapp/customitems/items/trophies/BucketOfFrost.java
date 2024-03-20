package wyattduber.cashapp.customitems.items.trophies;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.List;

public class BucketOfFrost {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.POWDER_SNOW_BUCKET, 64);
        ItemMeta eggMeta = item.getItemMeta();

        // Set Name
        eggMeta.displayName(Component.text(CashApp.replaceColors("&eEggsplosion")));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(CashApp.replaceColors("&8microwaved for 5 minutes, be careful")));
        eggMeta.lore(lore);

        // Set Enchantment Glow
        eggMeta.addEnchant(Enchantment.LUCK, 1, false);
        eggMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        eggMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set the finished modified meta and set the egg item to the created item
        item.setItemMeta(eggMeta);
        return item;
    }

}
