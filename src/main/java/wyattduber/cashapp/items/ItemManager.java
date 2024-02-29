package wyattduber.cashapp.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private static final CashApp ca = CashApp.getPlugin();
    public static ItemStack egg;
    public static ItemStack bow;
    public static ItemStack crossBow;

    public static void init() {
        createEgg();
        createBow();
        createCrossbow();
    }

    private static void createEgg() {
        ItemStack item = new ItemStack(Material.EGG, 64);
        ItemMeta eggMeta = item.getItemMeta();

        // Set Name
        eggMeta.displayName(Component.text(ca.replaceColors("&eEggsplosion")));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ca.replaceColors("&8microwaved for 5 minutes, be careful")));
        eggMeta.lore(lore);

        // Set Enchantment Glow
        eggMeta.addEnchant(Enchantment.LUCK, 1, false);
        eggMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        eggMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set the finished modified meta and set the egg item to the created item
        item.setItemMeta(eggMeta);
        egg = item;
    }
    private static void createBow() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = item.getItemMeta();

        // Set Name
        bowMeta.displayName(Component.text(ca.replaceColors("&8Wither's Penance")));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ca.replaceColors("p e w")));
        bowMeta.lore(lore);

        // Set the finished modified meta and set the egg item to the created item
        item.setItemMeta(bowMeta);
        bow = item;

        // Set Enchantments
        bow.addUnsafeEnchantment(Enchantment.MENDING, 1);
        bow.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 6);
        bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
    }
    private static void createCrossbow() {
        ItemStack item = new ItemStack(Material.CROSSBOW, 1);
        ItemMeta crossbowMeta = item.getItemMeta();

        // Set Name
        crossbowMeta.displayName(Component.text(ca.replaceColors("&8Wither's Penance")));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ca.replaceColors("p e w")));
        crossbowMeta.lore(lore);

        // Set the finished modified meta and set the egg item to the created item
        item.setItemMeta(crossbowMeta);
        crossBow = item;

        // Set Enchantments
        crossBow.addUnsafeEnchantment(Enchantment.PIERCING, 6);
        crossBow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 4);
        crossBow.addUnsafeEnchantment(Enchantment.MENDING, 1);
        crossBow.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
        crossBow.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
    }

}
