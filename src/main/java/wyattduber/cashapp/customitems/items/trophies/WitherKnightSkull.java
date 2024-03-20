package wyattduber.cashapp.customitems.items.trophies;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.List;

public class WitherKnightSkull {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.WITHER_SKELETON_SKULL, 64);
        ItemMeta eggMeta = item.getItemMeta();

        // Set Name
        eggMeta.displayName(Component.text("Wither Knight Skull", NamedTextColor.namedColor(1040)));

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
